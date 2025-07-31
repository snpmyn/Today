package litepal.crud;

import java.util.Collection;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.Contract;

import litepal.crud.model.AssociationsInfo;
import litepal.tablemanager.Connector;
import litepal.util.BaseUtility;
import litepal.util.DBUtility;
import timber.log.Timber;

/**
 * Deals analysis work when comes to two models are associated with Many2Many associations.
 *
 * @author Tony Green
 * @since 1.1
 */
public class Many2ManyAnalyzer extends AssociationsAnalyzer {
    /**
     * Analyzing the AssociationInfo. It will help baseObj assign the necessary
     * values automatically. If the two associated models have bidirectional
     * associations in class files but developer has only build unidirectional
     * associations in models, it will force to build the bidirectional
     * associations. Besides the
     * {@link LitePalSupport#addAssociatedModelForJoinTable(String, long)} will be called
     * here to put right values into tables.
     *
     * @param baseObj         The baseObj currently want to persist or update.
     * @param associationInfo The associated info analyzed by
     *                        {@link litepal.LitePalBase#getAssociationInfo(String)}.
     */
    void analyze(LitePalSupport baseObj, AssociationsInfo associationInfo) throws SecurityException, IllegalArgumentException, IllegalAccessException {
        Collection<LitePalSupport> associatedModels = getAssociatedModels(baseObj, associationInfo);
        declareAssociations(baseObj, associationInfo);
        if (associatedModels != null) {
            for (LitePalSupport associatedModel : associatedModels) {
                Collection<LitePalSupport> tempCollection = getReverseAssociatedModels(associatedModel, associationInfo);
                Collection<LitePalSupport> reverseAssociatedModels = checkAssociatedModelCollection(tempCollection, associationInfo.getAssociateSelfFromOtherModel());
                addNewModelForAssociatedModel(reverseAssociatedModels, baseObj);
                setReverseAssociatedModels(associatedModel, associationInfo, reverseAssociatedModels);
                dealAssociatedModel(baseObj, associatedModel);
            }
        }
    }

    /**
     * This add an empty set for {@link LitePalSupport#associatedModelsMapForJoinTable}.
     * Might use for updating intermediate join table.
     *
     * @param baseObj         The baseObj currently want to persist or update.
     * @param associationInfo To get associated table name from.
     */
    private void declareAssociations(@NonNull LitePalSupport baseObj, AssociationsInfo associationInfo) {
        baseObj.addEmptyModelForJoinTable(getAssociatedTableName(associationInfo));
    }

    /**
     * Force to build bidirectional associations for the associated model. If it
     * has already built, ignoring the rest process.
     *
     * @param associatedModelCollection The associated models collection of the associated model. Add
     *                                  self model into it if it doesn't contain self model yet.
     * @param baseObj                   The baseObj currently want to persist or update.
     */
    private void addNewModelForAssociatedModel(@NonNull Collection<LitePalSupport> associatedModelCollection, LitePalSupport baseObj) {
        if (!associatedModelCollection.contains(baseObj)) {
            associatedModelCollection.add(baseObj);
        }
    }

    /**
     * First of all the associated model need to be saved already, or nothing
     * will be executed below. Then add the id of associated model into
     * {@link LitePalSupport#associatedModelsMapForJoinTable} for
     * inserting value into intermediate join table after baseObj is saved.
     *
     * @param baseObj         The baseObj currently want to persist or update.
     * @param associatedModel The associated model of baseObj.
     */
    private void dealAssociatedModel(LitePalSupport baseObj, @NonNull LitePalSupport associatedModel) {
        if (associatedModel.isSaved()) {
            baseObj.addAssociatedModelForJoinTable(associatedModel.getTableName(), associatedModel.getBaseObjId());
        }
    }

    /**
     * Get the associated table name by {@link AssociationsInfo} after case
     * changed.
     *
     * @param associationInfo To get the associated table name from.
     * @return The name of associated table with changed case.
     */
    private String getAssociatedTableName(@NonNull AssociationsInfo associationInfo) {
        return BaseUtility.changeCase(DBUtility.getTableNameByClassName(associationInfo.getAssociatedClassName()));
    }

    /**
     * Check if the associations between self model and associated model is
     * already saved into intermediate join table.<br>
     * Make sure baseObj and associatedModel are saved already, or the result
     * might be wrong.
     *
     * @param baseObj         The baseObj currently want to persist or update.
     * @param associatedModel The associated model of baseObj.
     * @return If the associations between them is saved into intermediate join
     * table, return true. Otherwise return false.
     */
    private boolean isDataExists(LitePalSupport baseObj, LitePalSupport associatedModel) {
        boolean exists;
        SQLiteDatabase db = Connector.getDatabase();
        Cursor cursor = null;
        //noinspection TryFinallyCanBeTryWithResources
        try {
            cursor = db.query(getJoinTableName(baseObj, associatedModel), null, getSelection(baseObj, associatedModel), getSelectionArgs(baseObj, associatedModel), null, null, null);
            exists = cursor.getCount() > 0;
        } catch (Exception e) {
            Timber.e(e);
            return true;
        } finally {
            assert cursor != null;
            cursor.close();
        }
        return exists;
    }

    /**
     * Build the selection for querying the data in table. Column names are the
     * table names with _id as suffix.
     *
     * @param baseObj         The baseObj currently want to persist or update.
     * @param associatedModel The associated model of baseObj.
     * @return The selection clause for querying data.
     */
    @NonNull
    private String getSelection(@NonNull LitePalSupport baseObj, @NonNull LitePalSupport associatedModel) {
        return getForeignKeyColumnName(baseObj.getTableName()) + " = ? and " + getForeignKeyColumnName(associatedModel.getTableName()) + " = ?";
    }

    /**
     * Build the selection arguments to fill selection clause.
     *
     * @param baseObj         The baseObj currently want to persist or update.
     * @param associatedModel The associated model of baseObj.
     * @return The selection arguments with the id of baseObj and
     * associatedModel to fill.
     */
    @NonNull
    @Contract("_, _ -> new")
    private String[] getSelectionArgs(@NonNull LitePalSupport baseObj, @NonNull LitePalSupport associatedModel) {
        return new String[]{String.valueOf(baseObj.getBaseObjId()), String.valueOf(associatedModel.getBaseObjId())};
    }

    /**
     * Get the intermediate join table name for self model and associated model.
     *
     * @param baseObj         The baseObj currently want to persist or update.
     * @param associatedModel The associated model of baseObj.
     * @return The intermediate join table name.
     */
    private String getJoinTableName(LitePalSupport baseObj, @NonNull LitePalSupport associatedModel) {
        return getIntermediateTableName(baseObj, associatedModel.getTableName());
    }
}

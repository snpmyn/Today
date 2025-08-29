package litepal.crud;

import androidx.annotation.NonNull;

import java.util.Collection;

import litepal.crud.model.AssociationsInfo;
import litepal.util.DBUtility;

/**
 * Deals analysis work when comes to two models are associated with Many2One associations.
 *
 * @author Tony Green
 * @since 1.1
 */
class Many2OneAnalyzer extends AssociationsAnalyzer {
    /**
     * Analyzing the AssociationInfo. It will help baseObj assign the necessary
     * values automatically. If the two associated models have bidirectional
     * associations in class files but developer has only build unidirectional
     * associations in models, it will force to build the bidirectional
     * associations. Besides
     * {@link LitePalSupport#addAssociatedModelWithFK(String, long)} and
     * {@link LitePalSupport#addAssociatedModelWithoutFK(String, long)} will be
     * called here to put right values into tables.
     *
     * @param baseObj         The baseObj currently want to persist.
     * @param associationInfo The associated info analyzed by
     *                        <p>
     *                        {@link litepal.LitePalBase#getAssociationInfo(String)}.
     */
    void analyze(@NonNull LitePalSupport baseObj, @NonNull AssociationsInfo associationInfo) throws SecurityException, IllegalArgumentException, IllegalAccessException {
        if (baseObj.getClassName().equals(associationInfo.getClassHoldsForeignKey())) {
            analyzeManySide(baseObj, associationInfo);
        } else {
            analyzeOneSide(baseObj, associationInfo);
        }
    }

    /**
     * When it's on the M side. Get the associated model first, then use it to
     * get the associated model collection on the O side. Initialize the
     * collection by calling
     * {@link #checkAssociatedModelCollection(Collection, java.lang.reflect.Field)}
     * and calling
     * {@link #dealAssociatedModelOnManySide(Collection, LitePalSupport, LitePalSupport)}
     * to set foreign key.
     *
     * @param baseObj         The baseObj currently want to persist or update.
     * @param associationInfo The associated info analyzed by
     *                        {@link litepal.LitePalBase#getAssociationInfo(String)}.
     */
    private void analyzeManySide(LitePalSupport baseObj, AssociationsInfo associationInfo) throws SecurityException, IllegalArgumentException, IllegalAccessException {
        LitePalSupport associatedModel = getAssociatedModel(baseObj, associationInfo);
        if (associatedModel != null) {
            // now it's m2o bidirectional association.
            Collection<LitePalSupport> tempCollection = getReverseAssociatedModels(associatedModel, associationInfo);
            Collection<LitePalSupport> reverseAssociatedModels = checkAssociatedModelCollection(tempCollection, associationInfo.getAssociateSelfFromOtherModel());
            setReverseAssociatedModels(associatedModel, associationInfo, reverseAssociatedModels);
            dealAssociatedModelOnManySide(reverseAssociatedModels, baseObj, associatedModel);
        } else {
            mightClearFKValue(baseObj, associationInfo);
        }
    }

    /**
     * When it's on the O side. Get the associated model collection first, then
     * iterate all the associated models. Each associated model calls
     * {@link #buildBidirectionalAssociations(LitePalSupport, LitePalSupport, AssociationsInfo)} (LitePalSupport, LitePalSupport, org.litepal.crud.model.AssociationsInfo)}
     * to build bidirectional association if they haven't built yet. Then calls
     * {@link #dealAssociatedModelOnOneSide(LitePalSupport, LitePalSupport)} to set
     * foreign key.
     *
     * @param baseObj         The baseObj currently want to persist.
     * @param associationInfo The associated info analyzed by
     *                        {@link litepal.LitePalBase#getAssociationInfo(String)}.
     */
    private void analyzeOneSide(LitePalSupport baseObj, AssociationsInfo associationInfo) throws SecurityException, IllegalArgumentException, IllegalAccessException {
        Collection<LitePalSupport> associatedModels = getAssociatedModels(baseObj, associationInfo);
        if (associatedModels == null || associatedModels.isEmpty()) {
            String tableName = DBUtility.getTableNameByClassName(associationInfo.getAssociatedClassName());
            baseObj.addAssociatedTableNameToClearFK(tableName);
            return;
        }
        for (LitePalSupport associatedModel : associatedModels) {
            buildBidirectionalAssociations(baseObj, associatedModel, associationInfo);
            dealAssociatedModelOnOneSide(baseObj, associatedModel);
        }
    }

    /**
     * Check if the baseObj is already existed in the associatedModels
     * collection. If not add baseObj into the collection. Then if the
     * associated model is saved, add its' name and id to baseObj by calling
     * {@link LitePalSupport#addAssociatedModelWithFK(String, long)}.
     *
     * @param associatedModels The associated model collection.
     * @param baseObj          The baseObj currently want to persist.
     * @param associatedModel  The associated info analyzed by
     *                         {@link litepal.LitePalBase#getAssociationInfo(String)}.
     */
    private void dealAssociatedModelOnManySide(@NonNull Collection<LitePalSupport> associatedModels, LitePalSupport baseObj, LitePalSupport associatedModel) {
        if (!associatedModels.contains(baseObj)) {
            associatedModels.add(baseObj);
        }
        if (associatedModel.isSaved()) {
            baseObj.addAssociatedModelWithoutFK(associatedModel.getTableName(), associatedModel.getBaseObjId());
        }
    }

    /**
     * Deals with associated model on one side.
     *
     * @param baseObj         The baseObj currently want to persist.
     * @param associatedModel The associated info analyzed by
     *                        {@link litepal.LitePalBase#getAssociationInfo(String)}.
     */
    private void dealAssociatedModelOnOneSide(LitePalSupport baseObj, LitePalSupport associatedModel) {
        dealsAssociationsOnTheSideWithoutFK(baseObj, associatedModel);
    }
}
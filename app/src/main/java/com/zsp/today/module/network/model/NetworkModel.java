package com.zsp.today.module.network.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created on 2026/3/12.
 *
 * @author 郑少鹏
 * @desc 网络模型
 * <p>
 * Gson 在解析 JSON 时
 * 默认使用字段名直接匹配 JSON key
 * 如果字段名和 JSON key 不一致，就需要 @SerializedName 指定映射。
 * <p>
 * 保证字段映射稳定
 * 兼容 JSON 命名规范
 * 防止混淆（Proguard / R8）
 */
public class NetworkModel {
    @SerializedName("ip")
    private String ip;
    @SerializedName("network")
    private String network;
    @SerializedName("version")
    private String version;
    @SerializedName("city")
    private String city;
    @SerializedName("region")
    private String region;
    @SerializedName("region_code")
    private String regionCode;
    @SerializedName("country")
    private String country;
    @SerializedName("country_name")
    private String countryName;
    @SerializedName("country_code")
    private String countryCode;
    @SerializedName("country_code_iso3")
    private String countryCodeIso3;
    @SerializedName("country_capital")
    private String countryCapital;
    @SerializedName("country_tld")
    private String countryTld;
    @SerializedName("continent_code")
    private String continentCode;
    @SerializedName("in_eu")
    private Boolean inEu;
    @SerializedName("postal")
    private String postal;
    @SerializedName("latitude")
    private Double latitude;
    @SerializedName("longitude")
    private Double longitude;
    @SerializedName("timezone")
    private String timezone;
    @SerializedName("utc_offset")
    private String utcOffset;
    @SerializedName("country_calling_code")
    private String countryCallingCode;
    @SerializedName("currency")
    private String currency;
    @SerializedName("currency_name")
    private String currencyName;
    @SerializedName("languages")
    private String languages;
    @SerializedName("country_area")
    private Integer countryArea;
    @SerializedName("country_population")
    private Integer countryPopulation;
    @SerializedName("asn")
    private String asn;
    @SerializedName("org")
    private String org;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getNetwork() {
        return network;
    }

    public void setNetwork(String network) {
        this.network = network;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getRegionCode() {
        return regionCode;
    }

    public void setRegionCode(String regionCode) {
        this.regionCode = regionCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getCountryCodeIso3() {
        return countryCodeIso3;
    }

    public void setCountryCodeIso3(String countryCodeIso3) {
        this.countryCodeIso3 = countryCodeIso3;
    }

    public String getCountryCapital() {
        return countryCapital;
    }

    public void setCountryCapital(String countryCapital) {
        this.countryCapital = countryCapital;
    }

    public String getCountryTld() {
        return countryTld;
    }

    public void setCountryTld(String countryTld) {
        this.countryTld = countryTld;
    }

    public String getContinentCode() {
        return continentCode;
    }

    public void setContinentCode(String continentCode) {
        this.continentCode = continentCode;
    }

    public Boolean getInEu() {
        return inEu;
    }

    public void setInEu(Boolean inEu) {
        this.inEu = inEu;
    }

    public String getPostal() {
        return postal;
    }

    public void setPostal(String postal) {
        this.postal = postal;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getUtcOffset() {
        return utcOffset;
    }

    public void setUtcOffset(String utcOffset) {
        this.utcOffset = utcOffset;
    }

    public String getCountryCallingCode() {
        return countryCallingCode;
    }

    public void setCountryCallingCode(String countryCallingCode) {
        this.countryCallingCode = countryCallingCode;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getCurrencyName() {
        return currencyName;
    }

    public void setCurrencyName(String currencyName) {
        this.currencyName = currencyName;
    }

    public String getLanguages() {
        return languages;
    }

    public void setLanguages(String languages) {
        this.languages = languages;
    }

    public Integer getCountryArea() {
        return countryArea;
    }

    public void setCountryArea(Integer countryArea) {
        this.countryArea = countryArea;
    }

    public Integer getCountryPopulation() {
        return countryPopulation;
    }

    public void setCountryPopulation(Integer countryPopulation) {
        this.countryPopulation = countryPopulation;
    }

    public String getAsn() {
        return asn;
    }

    public void setAsn(String asn) {
        this.asn = asn;
    }

    public String getOrg() {
        return org;
    }

    public void setOrg(String org) {
        this.org = org;
    }
}
package datastax.com.dataObjects;

import com.datastax.oss.driver.api.mapper.annotations.ClusteringColumn;
import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.PartitionKey;

import java.time.Instant;
import java.util.Set;

@Entity
@CqlName("cam_search_v1")
public class CAMSearch {

    @PartitionKey private String accountNumber;
    @ClusteringColumn(0) private String opco;
    @ClusteringColumn(1) @CqlName("contact_type_code") private String contactTypeCode;
    @ClusteringColumn(2) @CqlName("contact_business_id") private String contactBusinessID;
    @CqlName("last_update_tmstp") private Instant lastUpdated;
    @CqlName("profile__archive_reason_code") private String profileArchiveReasonCode;
    @CqlName("profile__customer_account_status") private String profileCustAcctStatus;
    @CqlName("profile__account_type") private String profileAccountType;
    @CqlName("profile__airport_code") private String profileAirportCode;
    @CqlName("profile__synonym_name_1") private String profileSynonym1;
    @CqlName("profile__synonym_name_2") private String profileSynonym2;
    @CqlName("profile__interline_cd") private String profileInterlineCode;
    @CqlName("invoice_preference__billing_restriction_indicator") private String invoicePrefBillRestricInd;
    @CqlName("credit_detail__cash_only_reason") private String creditCashOnlyReason;
    @CqlName("credit_detail__credit_rating") private String creditCreditRating;
    @CqlName("contact_document_id") private long contactDocID;
    @CqlName("company_name") private String companyName;
    @CqlName("person__first_name") private String personFirstName;
    @CqlName("person__last_name") private String personLastName;
    @CqlName("person__middle_name") private String personMiddleName;
    @CqlName("address__street_line") private String addressStreetLine;
    @CqlName("address__additional_line1") private String addressAddLine1;
    @CqlName("address__geo_political_subdivision1") private String addressGeoPoliticalSub1;
    @CqlName("address__geo_political_subdivision2") private String addressGeoPoliticalSub2;
    @CqlName("address__geo_political_subdivision3") private String addressGeoPoliticalSub3;
    @CqlName("address__postal_code") private String addresPostalCode;
    @CqlName("address__country_code") private String addressCountryCode;
    @CqlName("share_id") private String shareID;
    @CqlName("email") private String email;
    @CqlName("tele_com") private Set<ContactTelecomDetails> teleCom;

    public CAMSearch() {};

    public String getAccountNumber() { return accountNumber;}
    public void setAccountNumber(String val) { accountNumber = val;}

    public String getOpco() {return opco;}
    public void setOpco(String val) {opco = val;}

    public String getContactTypeCode() {return contactTypeCode;}
    public void setContactTypeCode(String val) {contactTypeCode = val;}

    public String getContactBusinessID() {return contactBusinessID;}
    public void setContactBusinessID(String val) {contactBusinessID = val;}

    public String getProfileArchiveReasonCode() {return profileArchiveReasonCode;}
    public void setProfileArchiveReasonCode(String val) {profileArchiveReasonCode = val;}

    public String getProfileCustAcctStatus() {return profileCustAcctStatus;}
    public void setProfileCustAcctStatus(String val) {profileCustAcctStatus = val;}

    public String getProfileAccountType() {return profileAccountType;}
    public void setProfileAccountType(String val) {profileAccountType = val;}

    public String getProfileAirportCode() {return profileAirportCode;}
    public void setProfileAirportCode(String val) {profileAirportCode = val;}

    public String getProfileSynonym1() {return profileSynonym1;}
    public void setProfileSynonym1(String val) {profileSynonym1 = val;}

    public String getProfileSynonym2() {return profileSynonym2;}
    public void setProfileSynonym2(String val) {profileSynonym2 = val;}

    public String getProfileInterlineCode() {return profileInterlineCode;}
    public void setProfileInterlineCode(String val) {profileInterlineCode = val;}

    public String getInvoicePrefBillRestricInd() {return invoicePrefBillRestricInd;}
    public void setInvoicePrefBillRestricInd(String val) {invoicePrefBillRestricInd = val;}

    public String getCreditCashOnlyReason() {return creditCashOnlyReason;}
    public void setCreditCashOnlyReason(String val) {creditCashOnlyReason = val;}

    public String getCreditCreditRating() {return creditCreditRating;}
    public void setCreditCreditRating(String val) {creditCreditRating = val;}

    public String getCompanyName() {return companyName;}
    public void setCompanyName(String val) {companyName = val;}

    public String getPersonFirstName() {return personFirstName;}
    public void setPersonFirstName(String val) {personFirstName = val;}

    public String getPersonLastName() {return personLastName;}
    public void setPersonLastName(String val) {personLastName = val;}

    public String getPersonMiddleName() {return personMiddleName;}
    public void setPersonMiddleName(String val) {personMiddleName = val;}

    public String getAddressStreetLine() {return addressStreetLine;}
    public void setAddressStreetLine(String val) {addressStreetLine = val;}

    public String getAddressAddLine1() {return addressAddLine1;}
    public void setAddressAddLine1(String val) {addressAddLine1 = val;}

    public String getAddressGeoPoliticalSub1() {return addressGeoPoliticalSub1;}
    public void setAddressGeoPoliticalSub1(String val) {addressGeoPoliticalSub1 = val;}
    public String getAddressGeoPoliticalSub2() {return addressGeoPoliticalSub2;}
    public void setAddressGeoPoliticalSub2(String val) {addressGeoPoliticalSub2 = val;}
    public String getAddressGeoPoliticalSub3() {return addressGeoPoliticalSub3;}
    public void setAddressGeoPoliticalSub3(String val) {addressGeoPoliticalSub3 = val;}

    public String getAddresPostalCode() {return addresPostalCode;}
    public void setAddresPostalCode(String val) {addresPostalCode = val;}

    public String getAddressCountryCode() {return addressCountryCode;}
    public void setAddressCountryCode(String val) {addressCountryCode = val;}

    public String getShareID() {return shareID;}
    public void setShareID(String val) {shareID = val;}

    public String getEmail() {return email;}
    public void setEmail(String val) {email = val;}





    public Instant getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(Instant val) { lastUpdated = val;}

    public long getContactDocID() { return contactDocID; }
    public void setContactDocID(long val) { contactDocID = val;}

    public Set<ContactTelecomDetails> getTeleCom() { return teleCom; }
    public void setTeleCom(Set<ContactTelecomDetails> tc) { teleCom = tc; }
}

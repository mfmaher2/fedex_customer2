package datastax.com.dataObjects;

import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.SchemaHint;

@Entity
@CqlName("telecom_details_type")
@SchemaHint(targetElement = SchemaHint.TargetElement.UDT)
public class TelecomDetailsType {

    @CqlName("telecom_method") private String telecomMethod;
    @CqlName("numeric_country_code") private String numericCountryCode;
    @CqlName("alpha_country_code") private String aplhaCountryCode;
    @CqlName("area_code") private String areaCode;
    @CqlName("phone_number") private String phoneNumber;
    @CqlName("extension") private String extension;
    @CqlName("pin") private String pin;
    @CqlName("ftc_ok_to_call_flag") private boolean ftcOKToCallFlag;
    @CqlName("text_message_flag") private boolean textMessageFlag;

    public TelecomDetailsType() {};

    public String getTelecomMethod() { return telecomMethod; }
    public void setTelecomMethod(String val){ telecomMethod = val; }

    public String getNumericCountryCode() { return numericCountryCode; }
    public void setNumericCountryCode(String val){ numericCountryCode = val; }

    public String getAplhaCountryCode() { return aplhaCountryCode; }
    public void setAplhaCountryCode(String val){ aplhaCountryCode = val; }

    public String getAreaCode() { return areaCode; }
    public void setAreaCode(String val){ areaCode = val; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String val){ phoneNumber = val; }

    public String getExtension() { return extension; }
    public void setExtension(String val){ extension = val; }

    public String getPin() { return pin; }
    public void setPin(String val){ pin = val; }

    public boolean getFtcOKToCallFlag() { return ftcOKToCallFlag; }
    public void setFtcOKToCallFlag(boolean val){ ftcOKToCallFlag = val; }

    public boolean getTextMessageFlag() { return textMessageFlag; }
    public void setTextMessageFlag(boolean val){ textMessageFlag = val; }
}

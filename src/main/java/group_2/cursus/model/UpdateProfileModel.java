package group_2.cursus.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateProfileModel {
    
    @Valid
    @NotBlank(message = "name is required")
    @Size(min = 4, max = 200, message = "name must be between 4 and 200 characters")
    private String fullName;

    private boolean gender;

    private String address;

    @Pattern(regexp = "^(\\+\\d{1,3}[- ]?)?\\d{10}$", message = "phone number is not valid")
    private String phoneNumber;

    private String avatar = "/avatar.jpg";

    private String major;

    private String about;

    public UpdateProfileModel() {
        
    }
    
    public UpdateProfileModel(String fullName, boolean gender, String address, String phoneNumber, String avatar) {
        this.fullName = fullName;
        this.gender = gender;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.avatar = avatar;
    }
    
    public UpdateProfileModel(String fullName, boolean gender, String address, String phoneNumber, String avatar, String major) {
        this.fullName = fullName;
        this.gender = gender;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.avatar = avatar;
        this.major = major;
    }

    public UpdateProfileModel(String fullName, boolean gender, String address, String phoneNumber, String avatar, String major, String about) {
        this.fullName = fullName;
        this.gender = gender;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.avatar = avatar;
        this.major = major;
        this.about = about;
    }

    public String getFullName() {
        return fullName;
    }

    public boolean getGender() {
        return gender;
    }

    public String getAddress() {
        return address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getMajor() {
        return major;
    }

    public String getAbout() {
        return about;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public boolean isGender() {
        return gender;
    }

    public void setGender(boolean gender) {
        this.gender = gender;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public void setAbout(String about) {
        this.about = about;
    }
}

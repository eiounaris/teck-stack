package com.eiou.spring.validation;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class SignUpRequest {
    @NotBlank
    private String username;

    @NotBlank
    @Email
    private String email;

    @Min(18)
    private int age;

    @Valid
    private Address address;

    public SignUpRequest(String username, String email, int age, Address address) {
        this.username = username;
        this.email = email;
        this.age = age;
        this.address = address;
    }

    public static SignUpRequest valid() {
        return new SignUpRequest(
                "spring-user",
                "spring-user@example.com",
                20,
                new Address("Shanghai", "200000")
        );
    }

    public static SignUpRequest invalid() {
        return new SignUpRequest(
                "admin",
                "not-an-email",
                16,
                new Address("", "ABC")
        );
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public static class Address {
        @NotBlank
        private String city;

        @Pattern(regexp = "\\d{6}")
        private String zipCode;

        public Address(String city, String zipCode) {
            this.city = city;
            this.zipCode = zipCode;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getZipCode() {
            return zipCode;
        }

        public void setZipCode(String zipCode) {
            this.zipCode = zipCode;
        }
    }
}

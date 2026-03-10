package com.example.horserace.web.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class RegistrationForm {

    @NotBlank(message = "El nombre es obligatorio.")
    @Size(max = 120, message = "El nombre es demasiado largo.")
    private String fullName;

    @NotBlank(message = "El usuario es obligatorio.")
    @Size(min = 4, max = 40, message = "El usuario debe tener entre 4 y 40 caracteres.")
    @Pattern(regexp = "[a-zA-Z0-9._-]+", message = "El usuario solo admite letras, numeros, puntos, guiones y guion bajo.")
    private String username;

    @NotBlank(message = "La contrasena es obligatoria.")
    @Size(min = 6, max = 60, message = "La contrasena debe tener entre 6 y 60 caracteres.")
    private String password;

    @NotBlank(message = "Debes confirmar la contrasena.")
    private String confirmPassword;

    private Integer preferredGroupNumber;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public Integer getPreferredGroupNumber() {
        return preferredGroupNumber;
    }

    public void setPreferredGroupNumber(Integer preferredGroupNumber) {
        this.preferredGroupNumber = preferredGroupNumber;
    }
}

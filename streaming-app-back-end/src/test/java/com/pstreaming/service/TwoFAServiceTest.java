package com.pstreaming.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TwoFAServiceTest {

    @Mock
    private SmsService smsService;

    @InjectMocks
    private TwoFAService twoFAService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(twoFAService, "codeExpiration", 300L);
    }

    @Test
    void generateVerificationCode_isSixDigits() {
        assertThat(twoFAService.generateVerificationCode()).matches("\\d{6}");
    }

    @Test
    void sendVerificationCode_storesCodeAndSendsSms() {
        String code = twoFAService.sendVerificationCode("user@correo.com", "+50688887777");

        assertThat(code).matches("\\d{6}");
        verify(smsService).sendSms("+50688887777", "Your verification code is: " + code);
    }

    @Test
    void verifyCode_correctCode_returnsTrue() {
        String code = twoFAService.sendVerificationCode("user@correo.com", "+50688887777");

        assertThat(twoFAService.verifyCode("user@correo.com", code)).isTrue();
    }

    @Test
    void verifyCode_wrongCode_returnsFalse() {
        twoFAService.sendVerificationCode("user@correo.com", "+50688887777");

        assertThat(twoFAService.verifyCode("user@correo.com", "000000")).isFalse();
    }

    @Test
    void verifyCode_noActiveCode_returnsFalse() {
        assertThat(twoFAService.verifyCode("desconocido@correo.com", "123456")).isFalse();
    }

    @Test
    void verifyCode_isSingleUse() {
        String code = twoFAService.sendVerificationCode("user@correo.com", "+50688887777");

        assertThat(twoFAService.verifyCode("user@correo.com", code)).isTrue();
        assertThat(twoFAService.verifyCode("user@correo.com", code)).isFalse();
    }
}

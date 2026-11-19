package com._61119report.global.rsData;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class RsDataTest {

    @Test
    void statusCodeIsParsedFromResultCode() {
        assertThat(new RsData<>("201-1", "msg", "data").statusCode()).isEqualTo(201);
        assertThat(new RsData<>("404-2", "msg").statusCode()).isEqualTo(404);
        assertThat(new RsData<>("200-1-extra", "msg").statusCode()).isEqualTo(200);
    }

    @Test
    void dataIsNullWhenOmitted() {
        assertThat(new RsData<>("200-1", "msg").data()).isNull();
    }
}

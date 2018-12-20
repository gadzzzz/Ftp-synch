package com.synchftp.connector.api;

import com.synchftp.model.Response;
import com.synchftp.model.Setting;
import com.synchftp.service.SynchUtil;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.Map;

public interface Connector {
    void run(Setting setting, Object util, Map<String,Response> responseMap, SynchUtil synchUtil, boolean isProduction);
    ResponseEntity<Response> store(Setting setting, Object util,ResponseEntity<Response> response);
}

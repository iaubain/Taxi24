/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package com.gateway.logic;

import biz.galaxy.commons.config.CommonErrorCodeConfig;
import biz.galaxy.commons.models.ErrorModel;
import biz.galaxy.commons.models.ErrorsListModel;
import biz.galaxy.commons.utilities.ErrorGeneralException;
import com.gateway.config.SystemConfig;
import com.gateway.config.UrlConfig;
import com.gateway.remote.HttpCall;
import com.gateway.utilities.Log;
import com.gateway.utilities.ReturnUtil;
import static java.lang.System.out;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 *
 * @author Aubain
 */
@Component
public class RequestRouter {
    public ResponseEntity redirectPost (Map<String, String> hHeaders, String body, Map<String, String[]> params){
        try {
            Map<String, String> headers = new HashMap<>();
            for(Map.Entry<String, String> entry : hHeaders.entrySet()){
                String value = entry.getValue();
                if(value.length() > 2 && value.contains("[") && value.contains("]"))
                    value = value.substring(1, value.length()-1);
                String key = entry.getKey();
                headers.put(key, value);
            }
            String url = UrlConfig.SYSTEM_DISPATCHER;
            if(params != null && !params.isEmpty()){
                url = url + "?";
                for(Map.Entry<String, String[]> entry : params.entrySet()){
                    url += entry.getKey()+"="+entry.getValue()[0]+"&";
                }
                out.println("URL: "+url);
                url = url.substring(0, url.length()-1);
            }
            return new HttpCall().forwardPost(url, headers, body);
        } catch(ErrorGeneralException e){
            return ReturnUtil.isFailed(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }catch (Exception e) {
            Log.d(getClass(), e.getMessage());
            ErrorGeneralException error = new ErrorGeneralException(new ErrorsListModel(Arrays.asList(new ErrorModel(SystemConfig.SYSTEM_ID[0]+CommonErrorCodeConfig.GENERAL_PROCESSING_ERROR[0], CommonErrorCodeConfig.GENERAL_PROCESSING_ERROR[1], "There is an issue with request. Contact your system administrator."))));
            return ReturnUtil.isFailed(HttpStatus.BAD_REQUEST.value(), error.getMessage());
        }
    }
}

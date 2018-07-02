/** 
 * Copyright (C) 2018 thinh ho
 * This file is part of 'sample-aws' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */
package kkdt.sample.aws.cognito;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

import com.amazonaws.transform.MapEntry;

/**
 * Configurations for AWS - pulls properties and the application yaml files.
 * 
 * @author thinh ho
 *
 */
@Service
@ConfigurationProperties(prefix="idps")
public class AWS {
    private String defaultProvider;
    
    private Map<String, String> providers = new HashMap<>();
    
    @Value("${cognito.poolid:null}")
    private String poolId;
    
    @Value("${cognito.region:null}")
    private String region;
    
    @Value("${cognito.clientid:null}")
    private String clientId;
    
    @Value("${cognito.identitypool:null}")
    private String identityPool;
    
    @Value("${cognito.providerid:null}")
    private String defaultProviderId;

    public String getPoolId() {
        return poolId;
    }

    public void setPoolId(String poolId) {
        this.poolId = poolId;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getIdentityPool() {
        return identityPool;
    }

    public void setIdentityPool(String identityPool) {
        this.identityPool = identityPool;
    }

    public String getDefaultProviderId() {
        return defaultProviderId;
    }

    public void setDefaultProviderId(String defaultProviderId) {
        this.defaultProviderId = defaultProviderId;
    }

    public String getDefaultProvider() {
        return defaultProvider;
    }

    public void setDefaultProvider(String defaultProvider) {
        this.defaultProvider = defaultProvider;
    }

    public Map<String, String> getProviders() {
        return providers;
    }

    public void setProviders(Map<String, String> providers) {
        this.providers = providers;
    }

    public Set<Entry<String, String>> providers() {
        Set<Entry<String, String>> data = new HashSet<>(providers.entrySet());
        if(defaultProviderId != null && defaultProvider != null) {
            MapEntry<String, String> d = new MapEntry<String, String>();
            d.setKey(defaultProvider);
            d.setValue(defaultProviderId);
            data.add(d);
        }
        return data;
    }
}

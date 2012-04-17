/*
 * Copyright 2010-2011 Ning, Inc.
 *
 * Ning licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package com.ning.billing.overdue.config;

import java.net.URI;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.ning.billing.entitlement.api.user.SubscriptionBundle;
import com.ning.billing.util.config.ValidatingConfig;
import com.ning.billing.util.config.ValidationErrors;

@XmlRootElement(name="overdueConfig")
@XmlAccessorType(XmlAccessType.NONE)
public class OverdueConfig  extends ValidatingConfig<OverdueConfig> {

    @XmlElement(required=true, name="bundleOverdueStates")
    private OverdueStatesBundle bundleOverdueStates;

    public DefaultOverdueStateSet<SubscriptionBundle> getBundleStateSet() {
        return bundleOverdueStates;
    }


    @Override
    public ValidationErrors validate(OverdueConfig root,
            ValidationErrors errors) {
        return bundleOverdueStates.validate(root, errors);
    }
    
    public OverdueConfig setOverdueStatesBundle(OverdueStatesBundle bundleODS) {
        this.bundleOverdueStates = bundleODS;
        return this;
    }


    public URI getURI() {
        return null;
    }

}

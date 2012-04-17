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

package com.ning.billing.entitlement.api.overdue;

import com.google.inject.Inject;
import com.ning.billing.ErrorCode;
import com.ning.billing.catalog.api.CatalogService;
import com.ning.billing.entitlement.api.user.EntitlementUserApi;
import com.ning.billing.entitlement.api.user.EntitlementUserApiException;
import com.ning.billing.entitlement.api.user.Subscription;
import com.ning.billing.entitlement.api.user.SubscriptionBundle;
import com.ning.billing.overdue.OverdueAccessApi;
import com.ning.billing.overdue.config.api.OverdueState;

public class DefaultOverdueChecker implements OverdueChecker {

    private final EntitlementUserApi entitlementApi;

    @Inject
    public DefaultOverdueChecker(EntitlementUserApi entitlementApi, OverdueAccessApi overdueApi) {
        this.entitlementApi = entitlementApi;
    }

    @Override
    public void checkBlocked(Subscription subscription) throws EntitlementUserApiException {
        if(subscription.getBundleId() != null) {
            SubscriptionBundle bundle = entitlementApi.getBundleFromId(subscription.getBundleId());
            checkBlocked(bundle);
        }
    }

    @Override
    public void checkBlocked(SubscriptionBundle bundle) throws EntitlementUserApiException {
        OverdueState<SubscriptionBundle> bundleState = bundle.getOverdueState();
        if(bundleState != null && bundleState.blockChanges()) {
            throw new EntitlementUserApiException(ErrorCode.ENT_BUNDLE_IS_OVERDUE_BLOCKED, bundle.getId(), bundle.getKey());
        }
    }

 
}

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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;

import com.ning.billing.overdue.config.api.OverdueState;
import com.ning.billing.overdue.config.api.Overdueable;
import com.ning.billing.util.config.ValidatingConfig;
import com.ning.billing.util.config.ValidationError;
import com.ning.billing.util.config.ValidationErrors;

@XmlAccessorType(XmlAccessType.NONE)
public class DefaultOverdueState<T extends Overdueable> extends ValidatingConfig<OverdueConfig>  implements OverdueState<T> {

    private static final int MAX_NAME_LENGTH = 50;
    
    @XmlElement(required=false, name="condition")
	private DefaultCondition<T> condition;

	@XmlAttribute(required=true, name="name")
    @XmlID
    private String name; 

	@XmlElement(required=false, name="externalMessage")
	private String externalMessage = "";

    @XmlElement(required=false, name="disableEntitlementAndChangesBlocked")
    private Boolean disableEntitlement = false;
    
    @XmlElement(required=false, name="blockChanges")
    private Boolean blockChanges = false;
    
    @XmlElement(required=false, name="daysBetweenPaymentRetries")
    private Integer daysBetweenPaymentRetries = 8;
    
    @XmlElement(required=false, name="isClearState")
    private Boolean isClearState = false;
    
	//Other actions could include
	// - send email
	// - trigger payment retry?
	// - add tags to bundle/account
	// - set payment failure email template
	// - set payment retry interval
	// - backup payment mechanism?

	/* (non-Javadoc)
     * @see com.ning.billing.catalog.overdue.OverdueState#getStageName()
     */
	@Override
    public String getName() {
		return name;
	}

	/* (non-Javadoc)
     * @see com.ning.billing.catalog.overdue.OverdueState#getExternalMessage()
     */
	@Override
    public String getExternalMessage() {
		return externalMessage;
	}
	
    @Override
    public boolean blockChanges() {
        return blockChanges || disableEntitlement;
    }

	/* (non-Javadoc)
     * @see com.ning.billing.catalog.overdue.OverdueState#applyCancel()
     */
	@Override
    public boolean disableEntitlementAndChangesBlocked() {
		return disableEntitlement;
	}
	
	
    protected DefaultCondition<T> getCondition() {
		return condition;
	}

	protected DefaultOverdueState<T> setName(String name) {
		this.name = name;
		return this;
	}

	protected DefaultOverdueState<T> setExternalMessage(String externalMessage) {
		this.externalMessage = externalMessage;
		return this;
	}

    protected DefaultOverdueState<T> setDisableEntitlement(boolean cancel) {
        this.disableEntitlement = cancel;
        return this;
    }

    protected DefaultOverdueState<T> setBlockChanges(boolean cancel) {
        this.blockChanges = cancel;
        return this;
    }

	protected DefaultOverdueState<T> setCondition(DefaultCondition<T> condition) {
		this.condition = condition;
		return this;
	}

    @Override
    public boolean isClearState() {
        return isClearState;
    }

    @Override
    public ValidationErrors validate(OverdueConfig root,
            ValidationErrors errors) {
        if(name.length() > MAX_NAME_LENGTH) {
            errors.add(new ValidationError(String.format("Name of state '%s' exceeds the maximum length of %d",name,MAX_NAME_LENGTH),root.getURI(), DefaultOverdueState.class, name));
        }
        return errors;
    }

    @Override
    public int getDaysBetweenPaymentRetries() {
         return daysBetweenPaymentRetries;
    }


}

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

package com.ning.billing.util.validation;

import com.google.inject.Inject;
import com.ning.billing.util.validation.dao.DatabaseSchemaDao;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ValidationManager {
    private final DatabaseSchemaDao dao;

    // table name, string name, column info
    private final Map<String, Map<String, ColumnInfo>> columnInfoMap = new HashMap<String, Map<String, ColumnInfo>>();
    private final Map<Class, ValidationConfiguration> configurations = new HashMap<Class, ValidationConfiguration>();

    @Inject
    public ValidationManager(DatabaseSchemaDao dao) {
        this.dao = dao;
    }

    // replaces existing schema information with the information for the specified schema
    public void loadSchemaInformation(final String schemaName) {
        columnInfoMap.clear();

        // get schema information and map it to columnInfo
        List<ColumnInfo> columnInfoList = dao.getColumnInfoList(schemaName);
        for (ColumnInfo columnInfo : columnInfoList) {
            final String tableName = columnInfo.getTableName();

            if (!columnInfoMap.containsKey(tableName)) {
                columnInfoMap.put(tableName, new HashMap<String, ColumnInfo>());
            }

            columnInfoMap.get(tableName).put(columnInfo.getColumnName(), columnInfo);
        }
    }

    public Collection<ColumnInfo> getTableInfo(final String tableName) {
        return columnInfoMap.get(tableName).values();
    }

    public ColumnInfo getColumnInfo(final String tableName, final String columnName) {
        return (columnInfoMap.get(tableName) == null) ? null : columnInfoMap.get(tableName).get(columnName);
    }

    public boolean validate(Object o) {
        ValidationConfiguration configuration = getConfiguration(o.getClass());

        // if no configuration exists for this class, the object is valid
        if (configuration == null) {return true;}

        Class clazz = o.getClass();
        for (String propertyName : configuration.keySet()) {
            try {
                Field field = clazz.getDeclaredField(propertyName);
                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }

                Object value = field.get(o);

                ColumnInfo columnInfo = configuration.get(propertyName);
                if (columnInfo == null) {
                    // no column info means the property hasn't been properly mapped; suppress validation
                    return true;
                }

                if (!columnInfo.getIsNullable()) {
                    if (value == null) {return false;}
                }
            } catch (NoSuchFieldException e) {
                // if the field doesn't exist, assume the configuration is faulty and skip this property
            } catch (IllegalAccessException e) {
                // TODO: something? deliberate no op?
            }

        }

        return true;
    }

    public boolean hasConfiguration(Class clazz) {
        return configurations.containsKey(clazz);
    }

    public ValidationConfiguration getConfiguration(Class clazz) {
        return configurations.get(clazz);
    }

    public void setConfiguration(Class clazz, String propertyName, ColumnInfo columnInfo) {
        if (!configurations.containsKey(clazz)) {
            configurations.put(clazz, new ValidationConfiguration());
        }

        configurations.get(clazz).addMapping(propertyName, columnInfo);
    }
}

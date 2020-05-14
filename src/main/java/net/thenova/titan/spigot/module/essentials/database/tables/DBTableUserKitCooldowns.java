package net.thenova.titan.spigot.module.essentials.database.tables;

import net.thenova.titan.library.database.sql.table.DatabaseTable;
import net.thenova.titan.library.database.sql.table.column.TableColumn;
import net.thenova.titan.library.database.sql.table.column.data_type.BigInt;
import net.thenova.titan.library.database.sql.table.column.data_type.IntAutoIncrement;
import net.thenova.titan.library.database.sql.table.column.data_type.VarChar;
import net.thenova.titan.spigot.module.essentials.database.DatabaseEssentials;

/**
 * Copyright 2019 ipr0james
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public final class DBTableUserKitCooldowns extends DatabaseTable {

    public DBTableUserKitCooldowns() {
        super("user_kit_cooldowns", new DatabaseEssentials());
    }

    @Override
    public final void init() {
        registerColumn(
                new TableColumn("id", new IntAutoIncrement()).setPrimary(),
                new TableColumn("uuid", new VarChar(VarChar.LENGTH_UUID)).setNullable(false),
                new TableColumn("kit_name", new VarChar(VarChar.LENGTH_DEFAULT)).setNullable(false),
                new TableColumn("kit_cooldown", new BigInt()).setNullable(false).setDefault(0)
        );
    }
}

package net.thenova.titan.spigot.module.essentials.handler.warps;

import de.arraying.kotys.JSON;
import lombok.Getter;
import net.thenova.titan.library.file.FileHandler;
import net.thenova.titan.library.file.json.JSONFileData;
import net.thenova.titan.spigot.module.ModuleHandler;
import org.bukkit.Location;

import java.io.File;

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
@Getter
public class Warp {

    private final String name;
    private final Location location;

    public Warp(final File file) {
        final JSON json = FileHandler.INSTANCE.loadJSONFile(new DataFile(file)).getJSON();
    }

    private static final class DataFile implements JSONFileData {

        private final String name;

        public DataFile(final File file) {
            this.name = file.getName();
        }

        @Override
        public String name() {
            return this.name;
        }

        @Override
        public String path() {
            return ModuleHandler.INSTANCE.getDataFolder() + File.separator + "essentials" + File.separator + "warps";
        }

        @Override
        public ClassLoader loader() {
            return getClass().getClassLoader();
        }
    }
}

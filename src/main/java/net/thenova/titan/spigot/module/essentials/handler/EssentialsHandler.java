package net.thenova.titan.spigot.module.essentials.handler;

import de.arraying.kotys.JSON;
import de.arraying.kotys.JSONArray;
import net.thenova.titan.library.Titan;
import net.thenova.titan.library.file.FileHandler;
import net.thenova.titan.library.file.json.JSONFile;
import net.thenova.titan.spigot.module.ModuleHandler;
import net.thenova.titan.spigot.module.essentials.handler.kits.KitHandler;
import net.thenova.titan.spigot.module.essentials.handler.warps.WarpHandler;
import net.thenova.titan.spigot.users.user.User;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
public enum  EssentialsHandler {
    INSTANCE;

    private JSONFile config;
    private StarterItems starterItems;

    private List<String> motd;
    private List<String> rules;

    private final Map<String, List<String>> listGroups = new HashMap<>();

    /* All time player count */
    private int playerCount;

    public final void load() {
        final File dataFolder = new File(ModuleHandler.INSTANCE.getDataFolder(), "essentials");

        if(!dataFolder.exists()) {
            Titan.INSTANCE.getLogger().info("[Module] [Essentials] [EssentialsHandler] - Creating data folder. Status: {}",
                    dataFolder.mkdir() ? "success" : "failed");
        }

        this.config = FileHandler.INSTANCE.loadJSONFile(EssentialsDataFile.class);
        this.starterItems = new StarterItems(FileHandler.INSTANCE.loadJSONFile(StarterItemsDataFile.class));

        final File motd = new File(dataFolder, "motd.txt");
        if(!motd.exists()) {
            try {
                Titan.INSTANCE.getLogger().info("[Module] [Essentials] [EssentialsHandler] - Creating motd.txt file. Status: {}",
                        motd.createNewFile() ? "success" : "failed");
            } catch (IOException ex) {
                Titan.INSTANCE.getLogger().info("[Module] [Essentials] [EssentialsHandler] - Failed to .createNewFile for motd.txt", ex);
            }
        }
        this.motd = this.readContent(motd);

        final File rules = new File(dataFolder, "rules.txt");
        if(!rules.exists()) {
            try {
                Titan.INSTANCE.getLogger().info("[Module] [Essentials] [EssentialsHandler] - Creating rules.txt file. Status: {}",
                        rules.createNewFile() ? "success" : "failed");
            } catch (IOException ex) {
                Titan.INSTANCE.getLogger().info("[Module] [Essentials] [EssentialsHandler] - Failed to .createNewFile for rules.txt", ex);
            }
        }
        this.rules = this.readContent(rules);

        final File kitsFolder = new File(dataFolder, "kits");
        if(!kitsFolder.exists()) {
            Titan.INSTANCE.getLogger().info("[Module] [Essentials] [EssentialsHandler] - Creating kits folder. Status: {}",
                    kitsFolder.mkdir() ? "success" : "failed");
        }
        KitHandler.INSTANCE.load(kitsFolder);

        final File warpsFolder = new File(dataFolder, "warps");
        if(!warpsFolder.exists()) {
            Titan.INSTANCE.getLogger().info("[Module] [Essentials] [EssentialsHandler] - Creating warps folder. Status: {}",
                    warpsFolder.mkdir() ? "success" : "failed");
        }
        WarpHandler.INSTANCE.load(warpsFolder);

        final JSON json = this.config.getJSON().json("list-groups");

        json.raw().keySet().forEach(key -> {
            final JSONArray array = json.array(key);
            final List<String> groups = new ArrayList<>();

            for(int i = 0; i < array.length(); i++) {
                groups.add(array.string(i));
            }

            this.listGroups.put(key, groups);
        });

        this.playerCount = this.config.get("connection.first-join.total", Integer.class);
    }

    private List<String> readContent(final File file) {
        final List<String> rtn = new ArrayList<>();

        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new FileReader(file));
            String line;

            while((line = reader.readLine()) != null) {
                rtn.add(line);
            }
        } catch (FileNotFoundException ex) {
            Titan.INSTANCE.getLogger().info("[Module] [Essentials] [EssentialsHandler] - Could not find file ({})", file.toString(), ex);
        } catch (IOException ex) {
            Titan.INSTANCE.getLogger().info("[Module] [Essentials] [EssentialsHandler] - Exception when reading file ({})", file.toString(), ex);
        } finally {
            try {
                assert reader != null;
                reader.close();
            } catch (IOException ex) {
                Titan.INSTANCE.getLogger().info("[Module] [Essentials] [EssentialsHandler] - Exception when closing reader ({})", file.toString(), ex);
            }
        }

        return rtn;
    }

    public final JSONFile getConfig() {
        return this.config;
    }
    public final StarterItems getStarterItems() {
        return this.starterItems;
    }

    public final List<String> getMOTD() {
        return this.motd;
    }
    public final List<String> getRules() {
        return this.rules;
    }

    public final Map<String, List<String>> getListGroups() {
        return this.listGroups;
    }

    public int getPlayerCount() {
        return this.playerCount;
    }

    public void incrementPlayerCount() {
        this.playerCount++;
        this.config.set("connection.first-join.total", this.playerCount);
    }

    public int getHomeLimit(final User user) {
        if(user.hasPermission("essentials.homes.unlimited")) {
            return Integer.MAX_VALUE;
        }

        int limit = 0;
        final JSON json = this.config.getJSON().json("homes");

        for(final String key : json.raw().keySet()) {
            if(user.hasPermission("essentials.homes." + key) && json.integer(key) > limit) {
                limit = json.integer(key);
            }
        }

        return limit;
    }
}

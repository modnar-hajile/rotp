/*
 * Copyright 2015-2020 Ray Fowler
 * 
 * Licensed under the GNU General Public License, Version 3 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     https://www.gnu.org/licenses/gpl-3.0.html
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package rotp.ui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import rotp.Rotp;
import rotp.util.LanguageManager;
import rotp.util.sound.SoundManager;

public class UserPreferences {
    private static final String WINDOW_MODE = "GAME_SETTINGS_WINDOWED";
    private static final String BORDERLESS_MODE = "GAME_SETTINGS_BORDERLESS";
    private static final String FULLSCREEN_MODE = "GAME_SETTINGS_FULLSCREEN";
    private static final String GRAPHICS_LOW = "GAME_SETTINGS_GRAPHICS_LOW";
    private static final String GRAPHICS_MEDIUM = "GAME_SETTINGS_GRAPHICS_MED";
    private static final String GRAPHICS_HIGH = "GAME_SETTINGS_GRAPHICS_HIGH";
    private static final String AUTOCOLONIZE_YES = "GAME_SETTINGS_AUTOCOLONIZE_YES";
    private static final String AUTOCOLONIZE_NO = "GAME_SETTINGS_AUTOCOLONIZE_NO";
    private static final String AUTOBOMBARD_NO = "GAME_SETTINGS_AUTOBOMBARD_NO";
    private static final String AUTOBOMBARD_NEVER = "GAME_SETTINGS_AUTOBOMBARD_NEVER";
    private static final String AUTOBOMBARD_YES = "GAME_SETTINGS_AUTOBOMBARD_YES";
    private static final String AUTOBOMBARD_WAR = "GAME_SETTINGS_AUTOBOMBARD_WAR";
    private static final String AUTOBOMBARD_INVADE = "GAME_SETTINGS_AUTOBOMBARD_INVADE";
    private static final String TEXTURES_NO = "GAME_SETTINGS_TEXTURES_NO";
    private static final String TEXTURES_INTERFACE = "GAME_SETTINGS_TEXTURES_INTERFACE";
    private static final String TEXTURES_MAP = "GAME_SETTINGS_TEXTURES_MAP";
    private static final String TEXTURES_BOTH = "GAME_SETTINGS_TEXTURES_BOTH";
    
    
    private static final String PREFERENCES_FILE = "Remnants.cfg";
    private static final int MAX_BACKUP_TURNS = 10;
    private static final String keyFormat = "%-20s: ";
    private static boolean showMemory = false;
    private static boolean playMusic = true;
    private static boolean playSounds = true;
    private static boolean displayYear = true;
    private static boolean alwaysStarGates = false; // modnar: add option to always have Star Gates tech
    private static boolean alwaysThorium = false; // modnar: add option to always have Thorium Cells tech
    private static boolean challengeMode = false; // modnar: add option to give AI more initial resources
    private static boolean randomTechStart = false; // modnar: add option to start all Empires with 2 techs, no Artifacts
    private static int autoSaveTurns = 5; // modnar: add option to auto-save every n-turns
    private static boolean autoColonize = false;
    private static String autoBombardMode = AUTOBOMBARD_NO;
    private static String displayMode = WINDOW_MODE;
    private static String graphicsMode = GRAPHICS_HIGH;
    private static String texturesMode = TEXTURES_MAP; // modnar: set default texture to only map, no interface
    private static float uiTexturePct = 0.20f;
    private static int screenSizePct = 93;
    private static final HashMap<String, String> raceNames = new HashMap<>();
    private static int backupTurns = 0;

    public static boolean showMemory()      { return showMemory; }
    public static void toggleMemory()       { showMemory = !showMemory; save(); }
    public static boolean fullScreen()      { return displayMode.equals(FULLSCREEN_MODE); }
    public static boolean windowed()        { return displayMode.equals(WINDOW_MODE); }
    public static boolean borderless()      { return displayMode.equals(BORDERLESS_MODE); }
    public static String displayMode()      { return displayMode; }
    public static void toggleDisplayMode()   { 
        switch(displayMode) {
            case WINDOW_MODE:     displayMode = BORDERLESS_MODE; break;
            case BORDERLESS_MODE: displayMode = FULLSCREEN_MODE; break;
            case FULLSCREEN_MODE: displayMode = WINDOW_MODE; break;
            default:              displayMode = WINDOW_MODE; break;
        }
        save();
    }
    public static String graphicsMode()     { return graphicsMode; }
    public static void toggleGraphicsMode()   { 
        switch(graphicsMode) {
            case GRAPHICS_HIGH:   graphicsMode = GRAPHICS_MEDIUM; break;
            case GRAPHICS_MEDIUM: graphicsMode = GRAPHICS_LOW; break;
            case GRAPHICS_LOW:    graphicsMode = GRAPHICS_HIGH; break;
            default :             graphicsMode = GRAPHICS_HIGH; break;
        }
        save();
    }
    public static void toggleTexturesMode()   { 
        switch(texturesMode) {
            case TEXTURES_NO:        texturesMode = TEXTURES_INTERFACE; break;
            case TEXTURES_INTERFACE: texturesMode = TEXTURES_MAP; break;
            case TEXTURES_MAP:       texturesMode = TEXTURES_BOTH; break;
            case TEXTURES_BOTH:      texturesMode = TEXTURES_NO; break;
            default :                texturesMode = TEXTURES_BOTH; break;
        }
        save();
    }
    public static String texturesMode()     { return texturesMode; }
    public static boolean texturesInterface() { return texturesMode.equals(TEXTURES_INTERFACE) || texturesMode.equals(TEXTURES_BOTH); }
    public static boolean texturesMap()       { return texturesMode.equals(TEXTURES_MAP) || texturesMode.equals(TEXTURES_BOTH); }
    
    public static String autoColonizeMode()     { return autoColonize ? AUTOCOLONIZE_YES : AUTOCOLONIZE_NO; }
    public static void toggleAutoColonize()     { autoColonize = !autoColonize; save();  }
    public static boolean autoColonize()        { return autoColonize; }
    
    public static void toggleAutoBombard()     { 
        switch(autoBombardMode) {
            case AUTOBOMBARD_NO:     autoBombardMode = AUTOBOMBARD_NEVER; break;
            case AUTOBOMBARD_NEVER:  autoBombardMode = AUTOBOMBARD_YES; break;
            case AUTOBOMBARD_YES:    autoBombardMode = AUTOBOMBARD_WAR; break;
            case AUTOBOMBARD_WAR:    autoBombardMode = AUTOBOMBARD_INVADE; break;
            case AUTOBOMBARD_INVADE: autoBombardMode = AUTOBOMBARD_NO; break;
            default:                 autoBombardMode = AUTOBOMBARD_NO; break;
        }
        save();
    }
    public static String autoBombardMode()        { return autoBombardMode; }
    public static boolean autoBombardNo()         { return autoBombardMode.equals(AUTOBOMBARD_NO); }
    public static boolean autoBombardNever()      { return autoBombardMode.equals(AUTOBOMBARD_NEVER); }
    public static boolean autoBombardYes()        { return autoBombardMode.equals(AUTOBOMBARD_YES); }
    public static boolean autoBombardWar()        { return autoBombardMode.equals(AUTOBOMBARD_WAR); }
    public static boolean autoBombardInvading()   { return autoBombardMode.equals(AUTOBOMBARD_INVADE); }
    
    public static boolean playAnimations()  { return !graphicsMode.equals(GRAPHICS_LOW); }
    public static boolean antialiasing()    { return graphicsMode.equals(GRAPHICS_HIGH); }
    public static boolean playSounds()      { return playSounds; }
    public static void toggleSounds()       { playSounds = !playSounds;	save(); }
    public static boolean playMusic()       { return playMusic; }
    public static void toggleMusic()        { playMusic = !playMusic; save();  }
    public static boolean alwaysStarGates()  { return alwaysStarGates; } // modnar: add option to always have Star Gates tech
    public static boolean alwaysThorium()    { return alwaysThorium; } // modnar: add option to always have Thorium Cells tech
    public static boolean challengeMode()    { return challengeMode; } // modnar: add option to give AI more initial resources
    public static boolean randomTechStart()  { return randomTechStart; } // modnar: add option to start all Empires with 2 techs, no Artifacts
    public static int autoSaveTurns()       { return autoSaveTurns; } // modnar: add option to auto-save every n-turns
    public static void autoSaveTurns(int i) { setAutoSaveTurns(i); } // modnar: add option to auto-save every n-turns
    public static int screenSizePct()       { return screenSizePct; }
    public static void screenSizePct(int i) { setScreenSizePct(i); }
    public static int backupTurns()         { return backupTurns; }
    public static boolean backupTurns(int i)   { 
        int prev = backupTurns;
        backupTurns = Math.min(Math.max(0,i),MAX_BACKUP_TURNS); 
        save();
        return prev != backupTurns;
    }
    public static void toggleBackupTurns() {
        if (backupTurns >= MAX_BACKUP_TURNS)
            backupTurns = 0;
        else
            backupTurns++;
        save();
    }
    public static void toggleYearDisplay()    { displayYear = !displayYear; save(); }
    public static boolean displayYear()       { return displayYear; }
    public static void uiTexturePct(int i)    { uiTexturePct = i / 100.0f; }
    public static float uiTexturePct()        { return uiTexturePct; }

    
    public static void loadAndSave() {
        load();
        save();
    }
    public static void load() {
        String path = Rotp.jarPath();
        File configFile = new File(path, PREFERENCES_FILE);
		// modnar: change to InputStreamReader, force UTF-8
		try ( BufferedReader in = new BufferedReader( new InputStreamReader( new FileInputStream(configFile), "UTF-8"));) {
            String input;
            if (in != null) {
                while ((input = in.readLine()) != null)
                    loadPreferenceLine(input.trim());
            }
        }
        catch (FileNotFoundException e) {
            System.err.println(path+PREFERENCES_FILE+" not found.");
        }
        catch (IOException e) {
            System.err.println("UserPreferences.load -- IOException: "+ e.toString());
        }
    }
    public static void save() {
        String path = Rotp.jarPath();
        List<String> raceKeys = new ArrayList<>(raceNames.keySet());
        Collections.sort(raceKeys);
        try (FileOutputStream fout = new FileOutputStream(new File(path, PREFERENCES_FILE));
            // modnar: change to OutputStreamWriter, force UTF-8
            PrintWriter out = new PrintWriter(new OutputStreamWriter(fout, "UTF-8")); ) {
            out.println(keyFormat("DISPLAY_MODE")+displayModeToSettingName(displayMode));
            out.println(keyFormat("GRAPHICS")+graphicsModeToSettingName(graphicsMode));
            out.println(keyFormat("MUSIC")+ yesOrNo(playMusic));
            out.println(keyFormat("SOUNDS")+ yesOrNo(playSounds));
            out.println(keyFormat("MUSIC_VOLUME")+ SoundManager.musicLevel());
            out.println(keyFormat("SOUND_VOLUME")+ SoundManager.soundLevel());
            out.println(keyFormat("BACKUP_TURNS")+ backupTurns);
            out.println(keyFormat("AUTOCOLONIZE")+ yesOrNo(autoColonize));
            out.println(keyFormat("AUTOBOMBARD")+autoBombardToSettingName(autoBombardMode));
            out.println(keyFormat("TEXTURES")+texturesToSettingName(texturesMode));
            out.println(keyFormat("SHOW_MEMORY")+ yesOrNo(showMemory));
            out.println(keyFormat("DISPLAY_YEAR")+ yesOrNo(displayYear));
            out.println(keyFormat("SCREEN_SIZE_PCT")+ screenSizePct());
            out.println(keyFormat("UI_TEXTURE_LEVEL")+(int) (uiTexturePct()*100));
            out.println(keyFormat("ALWAYS_STAR_GATES")+ yesOrNo(alwaysStarGates)); // modnar: add option to always have Star Gates tech
            out.println(keyFormat("ALWAYS_THORIUM")+ yesOrNo(alwaysThorium)); // modnar: add option to always have Thorium Cells tech
            out.println(keyFormat("CHALLENGE_MODE")+ yesOrNo(challengeMode)); // modnar: add option to give AI more initial resources
            out.println(keyFormat("RANDOM_TECH_START")+ yesOrNo(randomTechStart)); // modnar: add option to start all Empires with 2 techs, no Artifacts
            out.println(keyFormat("AUTO_SAVE_TURNS")+ autoSaveTurns()); // modnar: add option to auto-save every n-turns
            out.println(keyFormat("LANGUAGE")+ languageDir());
            for (String raceKey: raceKeys) 
              out.println(keyFormat(raceKey)+raceNames.get(raceKey));
        }
        catch (IOException e) {
            System.err.println("UserPreferences.save -- IOException: "+ e.toString());
        }
    }
    private static String keyFormat(String s)  { return String.format(keyFormat, s); }
    
    private static void loadPreferenceLine(String line) {
        if (line.isEmpty())
            return;

        String[] args = line.split(":");
        if (args.length < 2)
            return;

        String key = args[0].toUpperCase().trim();
        String val = args[1].trim();
        if (key.isEmpty() || val.isEmpty())
                return;

        if (Rotp.logging)
            System.out.println("Key:"+key+"  value:"+val);
        switch(key) {
            case "DISPLAY_MODE":  displayMode = displayModeFromSettingName(val); return;
            case "GRAPHICS":     graphicsMode = graphicsModeFromSettingName(val); return;
            case "MUSIC":        playMusic = yesOrNo(val); return;
            case "SOUNDS":       playSounds = yesOrNo(val); return;
            case "MUSIC_VOLUME": SoundManager.musicLevel(Integer.valueOf(val)); return;
            case "SOUND_VOLUME": SoundManager.soundLevel(Integer.valueOf(val)); return;
            case "BACKUP_TURNS": backupTurns  = Integer.valueOf(val); return;
            case "AUTOCOLONIZE": autoColonize = yesOrNo(val); return;
            case "AUTOBOMBARD":  autoBombardMode = autoBombardFromSettingName(val); return;
            case "TEXTURES":     texturesMode = texturesFromSettingName(val); return;
            case "SHOW_MEMORY":  showMemory = yesOrNo(val); return;
            case "DISPLAY_YEAR": displayYear = yesOrNo(val); return;
            case "SCREEN_SIZE_PCT": screenSizePct(Integer.valueOf(val)); return;
            case "UI_TEXTURE_LEVEL": uiTexturePct(Integer.valueOf(val)); return;
            case "ALWAYS_STAR_GATES": alwaysStarGates = yesOrNo(val); return; // modnar: add option to always have Star Gates tech
            case "ALWAYS_THORIUM": alwaysThorium = yesOrNo(val); return; // modnar: add option to always have Thorium Cells tech
            case "CHALLENGE_MODE": challengeMode = yesOrNo(val); return; // modnar: add option to give AI more initial resources
            case "RANDOM_TECH_START": randomTechStart = yesOrNo(val); return; // modnar: add option to start all Empires with 2 techs, no Artifacts
            case "AUTO_SAVE_TURNS": autoSaveTurns(Integer.valueOf(val)); return; // modnar: add option to auto-save every n-turns
            case "LANGUAGE":     selectLanguage(val); return;
            default:
                raceNames.put(key, val); break;
        }
    }
    private static String yesOrNo(boolean b) {
        return b ? "YES" : "NO";
    }
    private static boolean yesOrNo(String s) {
        return s.equalsIgnoreCase("YES");
    }
    private static void selectLanguage(String s) {
        LanguageManager.selectLanguage(s);
    }
    private static String languageDir() {
        return LanguageManager.selectedLanguageDir();
    }
    // modnar: add option to auto-save every n-turns
    private static void setAutoSaveTurns(int i) {
        // bound value to be at least 0 (meaning no auto-saves)
        autoSaveTurns = (int)Math.max(0,i);
    }
    private static void setScreenSizePct(int i) {
        screenSizePct = Math.max(50,Math.min(i,100));
    }
    public static boolean shrinkFrame() {
        int oldSize = screenSizePct;
        setScreenSizePct(screenSizePct-5);
        return oldSize != screenSizePct;
    }
    public static boolean expandFrame() {
        int oldSize = screenSizePct;
        setScreenSizePct(screenSizePct+5);
        return oldSize != screenSizePct;
    }
    public static String raceNames(String id, String defaultNames) {
        String idUpper = id.toUpperCase();
        if (raceNames.containsKey(idUpper))
            return raceNames.get(idUpper);
        
        raceNames.put(idUpper, defaultNames);
        return defaultNames;
    }
    public static String displayModeToSettingName(String s) {
        switch(s) {
            case WINDOW_MODE:     return "Windowed";
            case BORDERLESS_MODE: return "Borderless";
            case FULLSCREEN_MODE: return "Fullscreen";
        }
        return "Windowed";
    }
    public static String displayModeFromSettingName(String s) {
        switch(s) {
            case "Windowed":   return WINDOW_MODE;
            case "Borderless": return BORDERLESS_MODE;
            case "Fullscreen": return FULLSCREEN_MODE;
        }
        return WINDOW_MODE;
    }
    public static String graphicsModeToSettingName(String s) {
        switch(s) {
            case GRAPHICS_LOW:    return "Low";
            case GRAPHICS_MEDIUM: return "Medium";
            case GRAPHICS_HIGH:   return "High";
        }
        return "High";
    }
    public static String graphicsModeFromSettingName(String s) {
        switch(s) {
            case "Low":    return GRAPHICS_LOW;
            case "Medium": return GRAPHICS_MEDIUM;
            case "High":   return GRAPHICS_HIGH;
        }
        return GRAPHICS_HIGH;
    }
    public static String autoBombardToSettingName(String s) {
        switch(s) {
            case AUTOBOMBARD_NO:     return "No";
            case AUTOBOMBARD_NEVER:  return "Never";
            case AUTOBOMBARD_YES:    return "Yes";
            case AUTOBOMBARD_WAR:    return "War";
            case AUTOBOMBARD_INVADE: return "Invade";
        }
        return "No";
    }
    public static String autoBombardFromSettingName(String s) {
        switch(s) {
            case "No":     return AUTOBOMBARD_NO;
            case "Never":  return AUTOBOMBARD_NEVER;
            case "Yes":    return AUTOBOMBARD_YES;
            case "War":    return AUTOBOMBARD_WAR;
            case "Invade": return AUTOBOMBARD_INVADE;
        }
        return AUTOBOMBARD_NO;
    }
    public static String texturesToSettingName(String s) {
        switch(s) {
            case TEXTURES_NO:         return "No";
            case TEXTURES_INTERFACE: return "Interface";
            case TEXTURES_MAP:       return "Map";
            case TEXTURES_BOTH:      return "Both";
        }
        return "Both";
    }
    public static String texturesFromSettingName(String s) {
        switch(s) {
            case "No":        return TEXTURES_NO;
            case "Interface": return TEXTURES_INTERFACE;
            case "Map":       return TEXTURES_MAP;
            case "Both":      return TEXTURES_BOTH;
        }
        return TEXTURES_BOTH;
    }
}

/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce



import net.ccbluex.liquidbounce.event.ClientShutdownEvent
import net.ccbluex.liquidbounce.event.EventManager
import net.ccbluex.liquidbounce.features.command.CommandManager
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleManager
import net.ccbluex.liquidbounce.features.special.AntiForge
import net.ccbluex.liquidbounce.features.special.BungeeCordSpoof
import net.ccbluex.liquidbounce.features.special.CombatManager
import net.ccbluex.liquidbounce.file.FileManager
import net.ccbluex.liquidbounce.script.ScriptManager
import net.ccbluex.liquidbounce.script.remapper.Remapper.loadSrg
import net.ccbluex.liquidbounce.tabs.BlocksTab
import net.ccbluex.liquidbounce.tabs.ExploitsTab
import net.ccbluex.liquidbounce.tabs.HeadsTab
import net.ccbluex.liquidbounce.ui.client.clickgui.ClickGui
import net.ccbluex.liquidbounce.ui.client.hud.HUD
import net.ccbluex.liquidbounce.ui.client.hud.HUD.Companion.createDefault
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.ClassUtils.hasForge
import net.ccbluex.liquidbounce.utils.ClientUtils
import net.ccbluex.liquidbounce.utils.InventoryUtils
import net.ccbluex.liquidbounce.utils.RotationUtils
import net.ccbluex.liquidbounce.utils.misc.SoundFxPlayer
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.network.FMLEventChannel
import net.minecraftforge.fml.common.network.NetworkRegistry

object LiquidBounce {
    lateinit var Users: String
    // Client information
    //110214

    const val CLIENT_NAME = "Fearless"
    const val CLIENT_VERSION = "110214 Dev"
    const val IN_DEV = false
    const val CLIENT_CREATOR = "FMCPE"
    const val MINECRAFT_VERSION = "1.8.9"
    const val CLIENT_CLOUD = "https://cloud.liquidbounce.net/LiquidBounce"
    val backGround = ResourceLocation("Insane/Background.png")
    var isStarting = false
    var Yaw = 0f
    var Pitch = 0f
    // Managers
    lateinit var moduleManager: ModuleManager
    lateinit var commandManager: CommandManager
    lateinit var eventManager: EventManager
    lateinit var fileManager: FileManager
    lateinit var scriptManager: ScriptManager
    lateinit var combatManager: CombatManager
    var channel: FMLEventChannel? = null

    // HUD & ClickGUI
    lateinit var hud: HUD
    lateinit var module: Module
    lateinit var clickGui: ClickGui
    // Update information
    var latestVersion = ""
    var displayedUpdateScreen=false
    // Menu Background
    var background: ResourceLocation? = null





    /**
     * Execute if client will be started
     */
    fun startClient() {
        isStarting = true
        // Create file manager
        fileManager = FileManager()

        // Crate event manager
        eventManager = EventManager()

        // Register listeners
        eventManager.registerListener(RotationUtils())
        eventManager.registerListener(AntiForge())
        eventManager.registerListener(BungeeCordSpoof())
        eventManager.registerListener(InventoryUtils())


        combatManager =CombatManager()
        eventManager.registerListener(LiquidBounce.combatManager);
        // Create command manager
        commandManager = CommandManager()

        // Load client fonts
        Fonts.loadFonts()

        // Setup module manager and register modules
        moduleManager = ModuleManager()
        moduleManager.registerModules()

        try {
            // Remapper
            loadSrg()

            // ScriptManager
            scriptManager = ScriptManager()
            scriptManager.loadScripts()
            scriptManager.enableScripts()

        } catch (throwable: Throwable) {
            ClientUtils.getLogger().error("Failed to load scripts.", throwable)
        }

        //play sound when everything done

        // Register commands
        commandManager.registerCommands()

        // Load configs
        fileManager.loadConfigs(fileManager.modulesConfig, fileManager.valuesConfig, fileManager.accountsConfig,
                fileManager.friendsConfig, fileManager.xrayConfig, fileManager.shortcutsConfig)

        // ClickGUI
        clickGui = ClickGui()
        fileManager.loadConfig(fileManager.clickGuiConfig)

        // Tabs (Only for Forge!)
        if (hasForge()) {
            BlocksTab()
            ExploitsTab()
            HeadsTab()
        }

        // Set HUD
        hud = createDefault()
        fileManager.loadConfig(fileManager.hudConfig)

        // Disable optifine fastrender
        ClientUtils.disableFastRender()

        // Set is starting status
        isStarting = false
    }


    /**
     * Execute if client will be stopped
     */
    fun stopClient() {

        // Call client shutdown
        eventManager.callEvent(ClientShutdownEvent())

        // Save all available configs
        fileManager.saveAllConfigs()

    }

}
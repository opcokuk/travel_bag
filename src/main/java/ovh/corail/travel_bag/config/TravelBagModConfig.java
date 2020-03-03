package ovh.corail.travel_bag.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.config.ConfigFileTypeHandler;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLPaths;

import java.nio.file.Path;
import java.util.function.Function;

public class TravelBagModConfig extends ModConfig {
    private static final TravelBagModConfigFileTypeHandler CONFIG_FILE_TYPE_HANDLER = new TravelBagModConfigFileTypeHandler();

    public TravelBagModConfig(ForgeConfigSpec spec, ModContainer container) {
        super(Type.SERVER, spec, container, String.format("%s-%s.toml", container.getModId(), Type.SERVER.extension()));
    }

    @Override
    public ConfigFileTypeHandler getHandler() {
        return CONFIG_FILE_TYPE_HANDLER;
    }


    private static class TravelBagModConfigFileTypeHandler extends ConfigFileTypeHandler {
        @Override
        public Function<ModConfig, CommentedFileConfig> reader(Path configBasePath) {
            // based on a mekanism solution
            if (configBasePath.endsWith("serverconfig")) {
                return super.reader(FMLPaths.CONFIGDIR.get());
            }
            return super.reader(configBasePath);
        }
    }
}

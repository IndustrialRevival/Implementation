package org.irmc.industrialrevival.api.display;

import com.google.common.base.Preconditions;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Display;
import org.bukkit.entity.TextDisplay;
import org.bukkit.metadata.MetadataValue;
import org.irmc.industrialrevival.api.display.builder.TextModelBuilder;
import org.irmc.industrialrevival.dock.IRDock;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Represents different colored blocks **(rectangles only)** that can be displayed in the game, each with specific orientation and positioning.
 * These blocks are used to visualize various surfaces and connections in the industrial system.
 *
 * @author balugaq
 */
@Getter
public enum ColorBlock {
    NORTH_VISIBLE(
            Component.text("北"), //! DO NOT TRANSLATE
            new Quaternionf().identity(),
            new Quaternionf().identity(),
            scale -> -scale * 0.55f,
            scale -> scale * 4f
    ),
    SOUTH_VISIBLE(
            Component.text("南"), //! DO NOT TRANSLATE
            new Quaternionf().rotationX((float) Math.toRadians(180)),
            new Quaternionf().identity(),
            scale -> -scale * 0.55f,
            scale -> scale * 4f
    ),
    UP_VISIBLE(
            Component.text("上"), //! DO NOT TRANSLATE
            new Quaternionf().rotationX((float) Math.toRadians(-90)),
            new Quaternionf().identity(),
            scale -> scale * 0.45f,
            scale -> scale * 4f
    ),
    DOWN_VISIBLE(
            Component.text("下"), //! DO NOT TRANSLATE
            new Quaternionf().rotationX((float) Math.toRadians(90)),
            new Quaternionf().identity(),
            scale -> scale * 0.45f,
            scale -> scale * 4f
    ),
    EAST_VISIBLE(
            Component.text("东"), //! DO NOT TRANSLATE
            new Quaternionf().rotationY((float) Math.toRadians(-90)),
            new Quaternionf().identity(),
            scale -> scale * 0.45f,
            scale -> scale * 4f
    ),
    WEST_VISIBLE(
            Component.text("西"), //! DO NOT TRANSLATE
            new Quaternionf().rotationY((float) Math.toRadians(90)),
            new Quaternionf().identity(),
            scale -> scale * 0.55f,
            scale -> scale * 4f
    );

    public static final ColorBlock[] DEFAULT_SURFACE = new ColorBlock[]{
            ColorBlock.NORTH_VISIBLE,
            ColorBlock.SOUTH_VISIBLE,
            ColorBlock.WEST_VISIBLE,
            ColorBlock.EAST_VISIBLE,
            ColorBlock.UP_VISIBLE,
            ColorBlock.DOWN_VISIBLE
    };
    private final Component baseString;
    private final Quaternionf leftRotation;
    private final Quaternionf rightRotation;
    private final Function<Float, Float> translationHandler;
    private final Function<Float, Float> scaleHandler;

    ColorBlock(@NotNull Component baseString, @NotNull Quaternionf leftRotation, @NotNull Quaternionf right_rotation, @NotNull Function<Float, Float> translationHandler, @NotNull Function<Float, Float> scaleHandler) {
        this.baseString = baseString;
        this.leftRotation = leftRotation;
        this.rightRotation = right_rotation;
        this.translationHandler = translationHandler;
        this.scaleHandler = scaleHandler;
    }

    ColorBlock(@NotNull Component baseString, @NotNull Quaternionf leftRotation, @NotNull Quaternionf right_rotation, @NotNull Function<Float, Float> translationHandler) {
        this(baseString, leftRotation, right_rotation, translationHandler, scale -> scale);
    }

    /**
     * Creates text displays for all default surfaces within the given corners using the specified color.
     *
     * @param corners the area to display
     * @param color the color to use
     * @return list of TextDisplay objects created
     */
    public static List<TextDisplay> makeSurface(@NotNull Corners corners, @NotNull Color color) {
        return makeSurface(corners, color, null);
    }

    /**
     * Creates text displays for all default surfaces within the given corners using the specified color and texture handler.
     *
     * @param corners the area to display
     * @param color the color to use
     * @param textureHandler optional texture handler for additional styling
     * @return list of TextDisplay objects created
     */
    public static List<TextDisplay> makeSurface(@NotNull Corners corners, @NotNull Color color, @Nullable TextureHandler textureHandler) {
        List<TextDisplay> displays = new ArrayList<>();
        for (ColorBlock value : DEFAULT_SURFACE) {
            displays.add(value.make(corners, color, textureHandler));
        }

        return displays;
    }

    public static List<TextDisplay> makeLiquid(@NotNull Corners corners, @NotNull Color color, @Nullable TextureHandler textureHandler) {
        Location tmp = new Location(corners.getWorld(), 0, 0, 0);
        for (double x = corners.getMinX(); x < corners.getMaxX(); x += 1) {
            for (double y = corners.getMinY(); y < corners.getMaxY(); y += 1) {
                for (double z = corners.getMinZ(); z < corners.getMaxZ(); z += 1) {
                    tmp.set(x, y, z).getBlock().setType(Material.LAVA);
                }
            }
        }
        return makeSurface(corners, color, textureHandler);
    }

    public static void connect(TextDisplay display1, TextDisplay display2, Consumer<Corners> consumer) {
        List<MetadataValue> values1 = display1.getMetadata("center");
        List<MetadataValue> values2 = display2.getMetadata("center");
        if (values1 == null || values2 == null || values1.isEmpty() || values2.isEmpty()) {
            return;
        }

        String scenter1 = values1.get(0).asString();
        String scenter2 = values2.get(0).asString();
        String[] split1 = scenter1.split(";");
        String[] split2 = scenter2.split(";");

        if (split1.length != 3 || split2.length != 3) {
            return;
        }

        World world = display1.getWorld();

        Location center1 = new Location(world, Double.parseDouble(split1[0]), Double.parseDouble(split1[1]), Double.parseDouble(split1[2]));
        Location center2 = new Location(world, Double.parseDouble(split2[0]), Double.parseDouble(split2[1]), Double.parseDouble(split2[2]));

        Corners corners = Corners.of(center1, center2);
        consumer.accept(corners);
    }

    @ParametersAreNonnullByDefault
    public static void connect(TextDisplay display1, TextDisplay display2, Color color) {
        connect(display1, display2, (corners) -> {
            //todo
        });
    }

    @ParametersAreNonnullByDefault
    public static void connect(TextDisplay display1, TextDisplay display2, Color color, TextureHandler handler) {
        connect(display1, display2, (corners) -> {
            //todo
        });
    }

    public void make(@NotNull Block block, @NotNull Color color) {
        make(block, color, null);
    }

    public void make(@NotNull Corners corners, @NotNull Color color) {
        make(corners, color, null);
    }

    public void make(@NotNull Block block, @NotNull Color color, @Nullable TextureHandler textureHandler) {
        make(Corners.of(block), color, textureHandler);
    }

    public TextDisplay make(@NotNull Corners corners, @NotNull Color color, @Nullable TextureHandler textureHandler) {
        Preconditions.checkArgument(corners != null, "Corners cannot be null");
        Preconditions.checkArgument(color != null, "Color cannot be null");
        Preconditions.checkArgument(corners.getWorld() != null, "World cannot be null");

        float scaleX = corners.getDistanceX();
        float scaleY = corners.getDistanceY();
        float scaleZ = corners.getDistanceZ();
        Location center = corners.center();
        var builder = new TextModelBuilder()
                .setLeftRotation(leftRotation)
                .setRightRotation(rightRotation)
                .setSize(getSize(scaleX, scaleY, scaleZ))
                .setTranslation(getTranslation(scaleX, scaleY, scaleZ))
                .backgroundColor(color)
                .text(this.getBaseString())
                .textOpacity((byte) 5)
                .brightness(new Display.Brightness(15, 15))
                .fixedMetaData(IRDock.getPlugin(), "center", center.getX() + ";" + center.getY() + ";" + center.getZ());

        if (textureHandler != null) {
            textureHandler.accept(corners, builder);
        }

        return generate(corners, builder);
    }

    public @NotNull Vector3f getTranslation(float scaleX, float scaleY, float scaleZ) {
        return switch (this) {
            case NORTH_VISIBLE -> new Vector3f(translationHandler.apply(scaleX), 0f, 0f);
            case SOUTH_VISIBLE -> new Vector3f(translationHandler.apply(scaleX), 0f, 0f);
            case WEST_VISIBLE -> new Vector3f(0f, 0f, translationHandler.apply(scaleZ));
            case EAST_VISIBLE -> new Vector3f(0f, 0f, translationHandler.apply(scaleZ));
            case UP_VISIBLE -> new Vector3f(translationHandler.apply(scaleX), 0f, 0f);
            case DOWN_VISIBLE -> new Vector3f(translationHandler.apply(scaleX), 0f, 0f);
            default -> new Vector3f(0f, 0f, 0f);
        };
    }

    public @NotNull Vector3f getSize(float scaleX, float scaleY, float scaleZ) {
        float fixedScaleX = scaleHandler.apply(scaleX);
        float fixedScaleY = scaleHandler.apply(scaleY);
        float fixedScaleZ = scaleHandler.apply(scaleZ);
        return switch (this) {
            case UP_VISIBLE -> new Vector3f(fixedScaleX, fixedScaleZ, 0f);
            case DOWN_VISIBLE -> new Vector3f(fixedScaleX, fixedScaleZ, 0f);
            case NORTH_VISIBLE -> new Vector3f(fixedScaleX, fixedScaleY, 0f);
            case SOUTH_VISIBLE -> new Vector3f(fixedScaleX, fixedScaleY, 0f);
            case WEST_VISIBLE -> new Vector3f(fixedScaleZ, fixedScaleY, 0f);
            case EAST_VISIBLE -> new Vector3f(fixedScaleZ, fixedScaleY, 0f);
            default -> new Vector3f();
        };
    }

    /**
     * Generates a TextDisplay at the appropriate location based on the block's position and type.
     *
     * @param corners defines the area where the display should be placed
     * @param builder the TextModelBuilder to use for creating the display
     * @return the created TextDisplay object
     */
    private TextDisplay generate(@NotNull Corners corners, @NotNull TextModelBuilder builder) {
        Location location;
        switch (this) {
            case UP_VISIBLE ->
                    location = new Location(corners.getWorld(), corners.getMinX(), corners.getMaxY(), corners.getMaxZ());
            case SOUTH_VISIBLE ->
                    location = new Location(corners.getWorld(), corners.getMaxX(), corners.getMaxY(), corners.getMinZ());
            case WEST_VISIBLE ->
                    location = new Location(corners.getWorld(), corners.getMaxX(), corners.getMinY(), corners.getMinZ());
            case NORTH_VISIBLE ->
                    location = new Location(corners.getWorld(), corners.getMaxX(), corners.getMinY(), corners.getMaxZ());
            default ->
                    location = new Location(corners.getWorld(), corners.getMinX(), corners.getMinY(), corners.getMinZ());
        }

        if (location == null) {
            return null;
        }

        return builder.buildAt(location);
    }
}
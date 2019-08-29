package ovh.corail.travel_bag.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.item.crafting.ShapelessRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.registries.ForgeRegistryEntry;
import ovh.corail.travel_bag.registry.ModItems;

import java.util.stream.IntStream;

import static ovh.corail.travel_bag.ModTravelBag.MOD_ID;

public class RecipeColoredTravelBag extends ShapelessRecipe {
    private final String group;
    private final ItemStack recipeOutput;
    private final NonNullList<Ingredient> recipeItems;

    public RecipeColoredTravelBag(ResourceLocation rl, String groupIn, ItemStack recipeOutputIn, NonNullList<Ingredient> recipeItemsIn) {
        super(rl, groupIn, recipeOutputIn, recipeItemsIn);
        this.group = groupIn;
        this.recipeOutput = recipeOutputIn;
        this.recipeItems = recipeItemsIn;
    }

    @Override
    public boolean matches(CraftingInventory inv, World world) {
        return getRecipeOutput().getItem() == ModItems.TRAVEL_BAG && getRecipeOutput().hasTag() && getRecipeOutput().getTag().contains("color", Constants.NBT.TAG_INT) && super.matches(inv, world);
    }

    @Override
    public ItemStack getCraftingResult(CraftingInventory inv) {
        int colorCode = getRecipeOutput().getTag().getInt("color");
        ItemStack stack = IntStream.range(0, inv.getSizeInventory()).mapToObj(inv::getStackInSlot).filter(currentStack -> currentStack.getItem() == ModItems.TRAVEL_BAG).findFirst().orElse(ItemStack.EMPTY);
        if (stack.isEmpty()) {
            return super.getCraftingResult(inv);
        }
        ItemStack result = stack.copy();
        result.getOrCreateTag().putInt("color", colorCode);
        return result;
    }

    public static class Serializer<T extends RecipeColoredTravelBag> extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<T> {
        public static final ResourceLocation NAME = new ResourceLocation(MOD_ID, "colored_travel_bag");
        final IRecipeFactory<T> factory;

        public Serializer(IRecipeFactory<T> factory) {
            this.factory = factory;
        }

        public T read(ResourceLocation recipeId, JsonObject json) {
            String s = JSONUtils.getString(json, "group", "");
            NonNullList<Ingredient> nonnulllist = readIngredients(JSONUtils.getJsonArray(json, "ingredients"));
            if (nonnulllist.isEmpty()) {
                throw new JsonParseException("No ingredients for shapeless recipe");
            } else if (nonnulllist.size() > 3 * 3) {
                throw new JsonParseException("Too many ingredients for shapeless recipe the max is " + (3 * 3));
            } else {
                ItemStack itemstack = ShapedRecipe.deserializeItem(JSONUtils.getJsonObject(json, "result"));
                return this.factory.create(recipeId, s, itemstack, nonnulllist);
            }
        }

        private static NonNullList<Ingredient> readIngredients(JsonArray jsonElements) {
            NonNullList<Ingredient> nonnulllist = NonNullList.create();
            for (int i = 0; i < jsonElements.size(); ++i) {
                Ingredient ingredient = Ingredient.deserialize(jsonElements.get(i));
                if (!ingredient.hasNoMatchingItems()) {
                    nonnulllist.add(ingredient);
                }
            }
            return nonnulllist;
        }

        @Override
        public T read(ResourceLocation recipeId, PacketBuffer buffer) {
            String s = buffer.readString(32767);
            int i = buffer.readVarInt();
            NonNullList<Ingredient> nonnulllist = NonNullList.withSize(i, Ingredient.EMPTY);
            for (int j = 0; j < nonnulllist.size(); ++j) {
                nonnulllist.set(j, Ingredient.read(buffer));
            }
            ItemStack itemstack = buffer.readItemStack();
            return this.factory.create(recipeId, s, itemstack, nonnulllist);
        }

        @Override
        public void write(PacketBuffer buffer, RecipeColoredTravelBag recipe) {
            buffer.writeString(recipe.group);
            buffer.writeVarInt(recipe.recipeItems.size());
            for (Ingredient ingredient : recipe.recipeItems) {
                ingredient.write(buffer);
            }
            buffer.writeItemStack(recipe.recipeOutput);
        }

        public interface IRecipeFactory<T extends RecipeColoredTravelBag> {
            T create(ResourceLocation rl, String group, ItemStack recipeOutput, NonNullList<Ingredient> recipeItems);
        }
    }
}

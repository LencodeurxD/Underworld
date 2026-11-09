package net.mcreator.underworldupdate.item;

import net.minecraft.world.level.Level;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;

import net.mcreator.underworldupdate.procedures.ClepsidreRightclickedProcedure;

public class ClepsidreItem extends Item {
	public ClepsidreItem() {
		super(new Item.Properties().stacksTo(1));
	}

	@Override
	public UseAnim getUseAnimation(ItemStack itemstack) {
		return UseAnim.BLOCK;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player entity, InteractionHand hand) {
		InteractionResultHolder<ItemStack> ar = super.use(world, entity, hand);
		ClepsidreRightclickedProcedure.execute(world, entity);
		return ar;
	}
}
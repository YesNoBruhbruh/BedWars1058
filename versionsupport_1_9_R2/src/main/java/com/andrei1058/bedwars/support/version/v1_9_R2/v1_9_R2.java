package com.andrei1058.bedwars.support.version.v1_9_R2;

import com.andrei1058.bedwars.api.arena.IArena;
import com.andrei1058.bedwars.api.arena.shop.ShopHolo;
import com.andrei1058.bedwars.api.arena.team.ITeam;
import com.andrei1058.bedwars.api.arena.team.TeamColor;
import com.andrei1058.bedwars.api.entity.Despawnable;
import com.andrei1058.bedwars.api.events.player.PlayerKillEvent;
import com.andrei1058.bedwars.api.exceptions.InvalidEffectException;
import com.andrei1058.bedwars.api.exceptions.InvalidSoundException;
import com.andrei1058.bedwars.api.language.Language;
import com.andrei1058.bedwars.api.language.Messages;
import com.andrei1058.bedwars.api.server.VersionSupport;
import com.andrei1058.bedwars.support.version.common.VersionCommon;
import net.minecraft.server.v1_9_R2.*;
import net.minecraft.server.v1_9_R2.Item;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.craftbukkit.v1_9_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_9_R2.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_9_R2.entity.CraftTNTPrimed;
import org.bukkit.craftbukkit.v1_9_R2.util.UnsafeList;
import org.bukkit.craftbukkit.v1_9_R2.CraftServer;
import org.bukkit.craftbukkit.v1_9_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_9_R2.inventory.CraftItemStack;
import org.bukkit.entity.*;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Team;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import static com.andrei1058.bedwars.api.language.Language.getMsg;

public class v1_9_R2 extends VersionSupport {

    private static int renderDistance = Bukkit.spigot().getConfig().getInt("world-settings.entity-tracking-range.players");

    public v1_9_R2(Plugin plugin, String name){
        super(plugin, name);
        try {
            setBedDestroySound("ENTITY_ENDERDRAGON_GROWL");
            setPlayerKillsSound("ENTITY_WOLF_HURT");
            setCountdownSound("ENTITY_CHICKEN_EGG");
            setBoughtSound("BLOCK_ANVIL_HIT");
            setInsuffMoneySound("ENTITY_ENDERMEN_TELEPORT");
            setEggBridgeEffect("MOBSPAWNER_FLAMES");
        } catch (InvalidSoundException | InvalidEffectException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void registerCommand(String name, Command clasa) {
        ((CraftServer) getPlugin().getServer()).getCommandMap().register(name, clasa);
    }

    @Override
    public void sendTitle(Player p, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        if (title != null) {
            if (!title.isEmpty()) {
                IChatBaseComponent bc = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + title + "\"}");
                PacketPlayOutTitle tit = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, bc);
                PacketPlayOutTitle length = new PacketPlayOutTitle(fadeIn, stay, fadeOut);
                ((CraftPlayer) p).getHandle().playerConnection.sendPacket(tit);
                ((CraftPlayer) p).getHandle().playerConnection.sendPacket(length);
            }
        }
        if (subtitle != null) {
            if (!subtitle.isEmpty()) {
                IChatBaseComponent bc = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + subtitle + "\"}");
                PacketPlayOutTitle tit = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, bc);
                PacketPlayOutTitle length = new PacketPlayOutTitle(fadeIn, stay, fadeOut);
                ((CraftPlayer) p).getHandle().playerConnection.sendPacket(tit);
                ((CraftPlayer) p).getHandle().playerConnection.sendPacket(length);
            }
        }
    }

    @Override
    public void hidePlayer(Player player, List<Player> players) {
        for (Player p : players){
            if (p == player) continue;
            p.hidePlayer(player);
        }
    }

    @Override
    public void minusAmount(Player p, org.bukkit.inventory.ItemStack i, int amount) {
        i.setAmount(i.getAmount() - amount);
    }

    public void spawnSilverfish(Location loc, ITeam bedWarsTeam, int speed, int health, int despawn, int damage) {
        new Despawnable(Silverfish.spawn(this, loc, bedWarsTeam, speed, health, despawn, damage), bedWarsTeam, despawn,
                Messages.SHOP_UTILITY_NPC_SILVERFISH_NAME, PlayerKillEvent.PlayerKillCause.SILVERFISH_FINAL_KILL, PlayerKillEvent.PlayerKillCause.SILVERFISH);
    }

    @Override
    public void spawnIronGolem(Location loc, ITeam bedWarsTeam, int speed, int health, int despawn) {
        new Despawnable(IGolem.spawn(this, loc, bedWarsTeam, speed, health, despawn), bedWarsTeam, despawn, Messages.SHOP_UTILITY_NPC_IRON_GOLEM_NAME,
                PlayerKillEvent.PlayerKillCause.IRON_GOLEM_FINAL_KILL, PlayerKillEvent.PlayerKillCause.IRON_GOLEM);
    }

    @Override
    public void playAction(Player p, String text) {
        CraftPlayer cPlayer = (CraftPlayer) p;
        IChatBaseComponent cbc = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + text + "\"}");
        PacketPlayOutChat ppoc = new PacketPlayOutChat(cbc, (byte) 2);
        cPlayer.getHandle().playerConnection.sendPacket(ppoc);
    }

    @Override
    public boolean isBukkitCommandRegistered(String name) {
        return ((CraftServer) getPlugin().getServer()).getCommandMap().getCommand(name) != null;
    }

    @SuppressWarnings("deprecation")
    @Override
    public org.bukkit.inventory.ItemStack getItemInHand(Player p) {
        return p.getItemInHand();
    }

    @Override
    public void hideEntity(org.bukkit.entity.Entity e, Player p) {
        PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(e.getEntityId());
        ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);

    }

    @Override
    public boolean isArmor(org.bukkit.inventory.ItemStack itemStack) {
        if (CraftItemStack.asNMSCopy(itemStack) == null) return false;
        if (CraftItemStack.asNMSCopy(itemStack).getItem() == null) return false;
        return CraftItemStack.asNMSCopy(itemStack).getItem() instanceof ItemArmor;
    }

    @Override
    public boolean isTool(org.bukkit.inventory.ItemStack itemStack) {
        if (CraftItemStack.asNMSCopy(itemStack) == null) return false;
        if (CraftItemStack.asNMSCopy(itemStack).getItem() == null) return false;
        return CraftItemStack.asNMSCopy(itemStack).getItem() instanceof ItemTool;
    }

    @Override
    public boolean isSword(org.bukkit.inventory.ItemStack itemStack) {
        if (CraftItemStack.asNMSCopy(itemStack) == null) return false;
        if (CraftItemStack.asNMSCopy(itemStack).getItem() == null) return false;
        return CraftItemStack.asNMSCopy(itemStack).getItem() instanceof ItemSword;
    }

    @Override
    public boolean isAxe(org.bukkit.inventory.ItemStack itemStack) {
        if (CraftItemStack.asNMSCopy(itemStack).getItem() == null) return false;
        return CraftItemStack.asNMSCopy(itemStack).getItem() instanceof ItemAxe;
    }

    @Override
    public boolean isBow(org.bukkit.inventory.ItemStack itemStack) {
        if (CraftItemStack.asNMSCopy(itemStack) == null) return false;
        if (CraftItemStack.asNMSCopy(itemStack).getItem() == null) return false;
        return CraftItemStack.asNMSCopy(itemStack).getItem() instanceof ItemBow;
    }

    @Override
    public boolean isProjectile(org.bukkit.inventory.ItemStack itemStack) {
        if (CraftItemStack.asNMSCopy(itemStack) == null) return false;
        if (CraftItemStack.asNMSCopy(itemStack).getItem() == null) return false;
        return CraftItemStack.asNMSCopy(itemStack).getItem() instanceof IProjectile;
    }

    @Override
    public void registerEntities() {
        registerEntity("ShopNPC", 120, VillagerShop.class);
        registerEntity("Silverfish2", 60, Silverfish.class);
        registerEntity("IGolem", 99, IGolem.class);
    }

    @Override
    public void spawnShop(Location loc, String name1, List<Player> players, IArena arena) {
        Location l = loc.clone();
        spawnVillager(l);
        for (Player p : players) {
            String[] nume = getMsg(p, name1).split(",");
            if (nume.length == 1) {
                ArmorStand a = createArmorStand(nume[0], l.clone().add(0, 1.85, 0));
                new ShopHolo(Language.getPlayerLanguage(p).getIso(), a, null, l, arena);
            } else {
                ArmorStand a = createArmorStand(nume[0], l.clone().add(0, 2.1, 0));
                ArmorStand b = createArmorStand(nume[1], l.clone().add(0, 1.85, 0));
                new ShopHolo(Language.getPlayerLanguage(p).getIso(), a, b, l, arena);
            }
        }
        for (ShopHolo sh : ShopHolo.getShopHolo()) {
            if (sh.getA() == arena) {
                sh.update();
            }
        }
    }

    @Override
    public double getDamage(org.bukkit.inventory.ItemStack i) {
        net.minecraft.server.v1_9_R2.ItemStack nmsStack = CraftItemStack.asNMSCopy(i);
        NBTTagCompound compound = (nmsStack.hasTag()) ? nmsStack.getTag() : new NBTTagCompound();
        //noinspection ConstantConditions
        return compound.getDouble("generic.attackDamage");
    }

    private static ArmorStand createArmorStand(String name, Location loc) {
        ArmorStand a = loc.getWorld().spawn(loc, ArmorStand.class);
        a.setGravity(false);
        a.setVisible(false);
        a.setCustomNameVisible(true);
        a.setCustomName(name);
        return a;
    }


    private void registerEntity(String name, int id, Class customClass) {
        try {
            ArrayList<Map> dataMap = new ArrayList<>();
            for (Field f : EntityTypes.class.getDeclaredFields()) {
                if (!f.getType().getSimpleName().equals(Map.class.getSimpleName())) continue;
                f.setAccessible(true);
                dataMap.add((Map) f.get(null));
            }
            if (dataMap.get(2).containsKey(id)) {
                dataMap.get(0).remove(name);
                dataMap.get(2).remove(id);
            }
            Method method = EntityTypes.class.getDeclaredMethod("a", Class.class, String.class, Integer.TYPE);
            method.setAccessible(true);
            method.invoke(null, customClass, name, id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class VillagerShop extends net.minecraft.server.v1_9_R2.EntityVillager {
        VillagerShop(net.minecraft.server.v1_9_R2.World world) {
            super(world);
            try {
                Field bField = net.minecraft.server.v1_9_R2.PathfinderGoalSelector.class.getDeclaredField("b");
                bField.setAccessible(true);
                Field cField = net.minecraft.server.v1_9_R2.PathfinderGoalSelector.class.getDeclaredField("c");
                cField.setAccessible(true);
                bField.set(this.goalSelector, new UnsafeList());
                bField.set(this.targetSelector, new UnsafeList());
                cField.set(this.goalSelector, new UnsafeList());
                cField.set(this.targetSelector, new UnsafeList());
            } catch (Exception ignored) {
            }
            this.goalSelector.a(0, new net.minecraft.server.v1_9_R2.PathfinderGoalFloat(this));
            this.goalSelector.a(9, new net.minecraft.server.v1_9_R2.PathfinderGoalInteract(this, net.minecraft.server.v1_9_R2.EntityHuman.class, 3.0f, 1.0f));
            this.goalSelector.a(10, new net.minecraft.server.v1_9_R2.PathfinderGoalLookAtPlayer(this, net.minecraft.server.v1_9_R2.EntityHuman.class, 8.0f));
        }

        @Override
        public void move(double d0, double d1, double d2) {
        }

        @Override
        public void collide(net.minecraft.server.v1_9_R2.Entity entity) {
        }

        @Override
        public boolean damageEntity(net.minecraft.server.v1_9_R2.DamageSource damagesource, float f) {
            return false;
        }

        @Override
        public void g(double d0, double d1, double d2) {
        }

        public void a(SoundEffect soundeffect, float f, float f1) {
        }

        @Override
        protected void initAttributes() {
            super.initAttributes();
            this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.0D);
        }
    }

    @SuppressWarnings("UnusedReturnValue")
    private Villager spawnVillager(Location loc) {
        net.minecraft.server.v1_9_R2.WorldServer mcWorld = ((CraftWorld) loc.getWorld()).getHandle();
        VillagerShop customEnt = new VillagerShop(mcWorld);
        customEnt.setLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
        ((CraftLivingEntity) customEnt.getBukkitEntity()).setRemoveWhenFarAway(false);
        mcWorld.addEntity(customEnt, CreatureSpawnEvent.SpawnReason.CUSTOM);
        return (Villager) customEnt.getBukkitEntity();
    }

    @Override
    public void setSource(TNTPrimed tnt, Player owner) {
        EntityLiving nmsEntityLiving = (((CraftLivingEntity) owner).getHandle());
        EntityTNTPrimed nmsTNT = (((CraftTNTPrimed) tnt).getHandle());
        try {
            Field sourceField = EntityTNTPrimed.class.getDeclaredField("source");
            sourceField.setAccessible(true);
            sourceField.set(nmsTNT, nmsEntityLiving);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void voidKill(Player p) {
        ((CraftPlayer) p).getHandle().damageEntity(DamageSource.OUT_OF_WORLD, 1000);
    }

    @Override
    public void hideArmor(Player p, Player p2) {
        PacketPlayOutEntityEquipment hand1 = new PacketPlayOutEntityEquipment(p.getEntityId(), EnumItemSlot.MAINHAND, new ItemStack(Item.getById(0)));
        PacketPlayOutEntityEquipment hand2 = new PacketPlayOutEntityEquipment(p.getEntityId(), EnumItemSlot.OFFHAND, new ItemStack(Item.getById(0)));
        PacketPlayOutEntityEquipment helmet = new PacketPlayOutEntityEquipment(p.getEntityId(), EnumItemSlot.HEAD, new ItemStack(Item.getById(0)));
        PacketPlayOutEntityEquipment chest = new PacketPlayOutEntityEquipment(p.getEntityId(), EnumItemSlot.CHEST, new ItemStack(Item.getById(0)));
        PacketPlayOutEntityEquipment pants = new PacketPlayOutEntityEquipment(p.getEntityId(), EnumItemSlot.LEGS, new ItemStack(Item.getById(0)));
        PacketPlayOutEntityEquipment boots = new PacketPlayOutEntityEquipment(p.getEntityId(), EnumItemSlot.FEET, new ItemStack(Item.getById(0)));
        PlayerConnection pc = ((CraftPlayer) p2).getHandle().playerConnection;
        if (p != p2) {
            pc.sendPacket(hand1);
            pc.sendPacket(hand2);
        }
        pc.sendPacket(helmet);
        pc.sendPacket(chest);
        pc.sendPacket(pants);
        pc.sendPacket(boots);
    }

    @Override
    public void hidePlayer(Player victim, Player p) {
        if (victim == p) return;
        if (!victim.getLocation().getWorld().equals(p.getWorld())) return;
        if (victim.getLocation().distanceSquared(p.getLocation()) <= renderDistance) {
            PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(victim.getEntityId());
            ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
        }
    }

    @Override
    public void showPlayer(Player victim, Player p) {
        if (victim == p) return;
        PacketPlayOutNamedEntitySpawn s = new PacketPlayOutNamedEntitySpawn(((CraftPlayer)victim).getHandle());
        ((CraftPlayer)p).getHandle().playerConnection.sendPacket(s);
    }

    @Override
    public void showPlayer(Player whoToShow, List<Player> p) {
        for (Player p1 : p){
            if (p1.equals(whoToShow)) continue;
            p1.showPlayer(whoToShow);
        }
    }

    @Override
    public void showArmor(Player p, Player p2) {
        PacketPlayOutEntityEquipment hand1 = new PacketPlayOutEntityEquipment(p.getEntityId(), EnumItemSlot.MAINHAND, CraftItemStack.asNMSCopy(p.getInventory().getItemInMainHand()));
        PacketPlayOutEntityEquipment hand2 = new PacketPlayOutEntityEquipment(p.getEntityId(), EnumItemSlot.OFFHAND, CraftItemStack.asNMSCopy(p.getInventory().getItemInOffHand()));
        PacketPlayOutEntityEquipment helmet = new PacketPlayOutEntityEquipment(p.getEntityId(), EnumItemSlot.HEAD, CraftItemStack.asNMSCopy(p.getInventory().getHelmet()));
        PacketPlayOutEntityEquipment chest = new PacketPlayOutEntityEquipment(p.getEntityId(), EnumItemSlot.CHEST, CraftItemStack.asNMSCopy(p.getInventory().getChestplate()));
        PacketPlayOutEntityEquipment pants = new PacketPlayOutEntityEquipment(p.getEntityId(), EnumItemSlot.LEGS, CraftItemStack.asNMSCopy(p.getInventory().getLeggings()));
        PacketPlayOutEntityEquipment boots = new PacketPlayOutEntityEquipment(p.getEntityId(), EnumItemSlot.FEET, CraftItemStack.asNMSCopy(p.getInventory().getBoots()));
        EntityPlayer pc = ((CraftPlayer) p2).getHandle();
        if (p != p2) {
            pc.playerConnection.sendPacket(hand1);
            pc.playerConnection.sendPacket(hand2);
        }
        pc.playerConnection.sendPacket(helmet);
        pc.playerConnection.sendPacket(chest);
        pc.playerConnection.sendPacket(pants);
        pc.playerConnection.sendPacket(boots);
    }

    @Override
    public void spawnDragon(Location l, ITeam bwt) {
        EnderDragon ed = (EnderDragon) l.getWorld().spawnEntity(l, EntityType.ENDER_DRAGON);
        ed.setMetadata("DragonTeam", new FixedMetadataValue(getPlugin(), bwt));
        ed.setPhase(EnderDragon.Phase.CIRCLING);
    }

    @Override
    public void colorBed(ITeam bwt) {

    }

    @Override
    public void registerTntWhitelist() {
        try {
            Field field = Block.class.getDeclaredField("durability");
            field.setAccessible(true);
            field.set(Block.getByName("glass"), 300f);
            field.set(Block.getByName("stained_glass"), 300f);
            field.set(Block.getByName("end_stone"), 69f);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void setBlockTeamColor(org.bukkit.block.Block block, TeamColor teamColor) {
        block.setData(TeamColor.itemColor(teamColor));
    }

    @Override
    public void setCollide(Player p, IArena a, boolean value) {
        p.setCollidable(value);
        if (a != null) a.updateSpectatorCollideRule(p, value);
    }

    @Override
    public org.bukkit.inventory.ItemStack addCustomData(org.bukkit.inventory.ItemStack i, String data) {
        ItemStack itemStack = CraftItemStack.asNMSCopy(i);
        NBTTagCompound tag = itemStack.getTag();
        if (tag == null) {
            tag = new NBTTagCompound();
            itemStack.setTag(tag);
        }

        tag.setString("BedWars1058", data);
        return CraftItemStack.asBukkitCopy(itemStack);
    }

    @Override
    public boolean isCustomBedWarsItem(org.bukkit.inventory.ItemStack i) {
        ItemStack itemStack = CraftItemStack.asNMSCopy(i);
        NBTTagCompound tag = itemStack.getTag();
        if (tag == null) return false;
        return tag.hasKey("BedWars1058");
    }

    @Override
    public String getCustomData(org.bukkit.inventory.ItemStack i) {
        ItemStack itemStack = CraftItemStack.asNMSCopy(i);
        NBTTagCompound tag = itemStack.getTag();
        if (tag == null) return "";
        return tag.getString("BedWars1058");
    }

    @Override
    public org.bukkit.inventory.ItemStack setSkullOwner(org.bukkit.inventory.ItemStack i, Player p) {
        if (i.getType() != org.bukkit.Material.SKULL_ITEM) return i;
        SkullMeta sm = (SkullMeta) i.getItemMeta();
        sm.setOwner(p.getName());
        i.setItemMeta(sm);
        return i;
    }

    @Override
    public org.bukkit.inventory.ItemStack colourItem(org.bukkit.inventory.ItemStack itemStack, ITeam bedWarsTeam) {
        if (itemStack == null) return null;
        switch (itemStack.getType().toString()) {
            default:
                return itemStack;
            case "WOOL":
            case "STAINED_CLAY":
            case "STAINED_GLASS":
                return new org.bukkit.inventory.ItemStack(itemStack.getType(), itemStack.getAmount(),TeamColor.itemColor(bedWarsTeam.getColor()));
            case "GLASS":
                return new org.bukkit.inventory.ItemStack(org.bukkit.Material.STAINED_GLASS, itemStack.getAmount(), TeamColor.itemColor(bedWarsTeam.getColor()));
        }
    }

    @Override
    public org.bukkit.inventory.ItemStack createItemStack(String material, int amount, short data) {
        org.bukkit.inventory.ItemStack i;
        try {
            i = new org.bukkit.inventory.ItemStack(org.bukkit.Material.valueOf(material), amount, data);
        } catch (Exception ex) {
            getPlugin().getLogger().log(Level.WARNING, material + " is not a valid " + getName() + " material!");
            i = new org.bukkit.inventory.ItemStack(org.bukkit.Material.BEDROCK);
        }
        return i;
    }

    @Override
    public void teamCollideRule(Team team) {
        team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
        team.setCanSeeFriendlyInvisibles(true);
    }

    @Override
    public boolean isPlayerHead(String material, int data) {
        return material.equals("SKULL_ITEM") && data == 3;
    }

    @Override
    public org.bukkit.Material materialFireball() {
        return org.bukkit.Material.FIREBALL;
    }

    @Override
    public org.bukkit.Material materialPlayerHead() {
        return org.bukkit.Material.SKULL_ITEM;
    }

    @Override
    public org.bukkit.Material materialSnowball() {
        return org.bukkit.Material.SNOW_BALL;
    }

    @Override
    public org.bukkit.Material materialGoldenHelmet() {
        return org.bukkit.Material.GOLD_HELMET;
    }

    @Override
    public org.bukkit.Material materialGoldenChestPlate() {
        return org.bukkit.Material.GOLD_CHESTPLATE;
    }

    @Override
    public org.bukkit.Material materialGoldenLeggings() {
        return org.bukkit.Material.GOLD_LEGGINGS;
    }

    @Override
    public org.bukkit.Material materialCake() {
        return org.bukkit.Material.CAKE_BLOCK;
    }

    @Override
    public org.bukkit.Material materialCraftingTable() {
        return org.bukkit.Material.WORKBENCH;
    }

    @Override
    public org.bukkit.Material materialEnchantingTable() {
        return org.bukkit.Material.ENCHANTMENT_TABLE;
    }

    @Override
    public boolean isBed(org.bukkit.Material material) {
        return material == org.bukkit.Material.BED_BLOCK || material == org.bukkit.Material.BED;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean itemStackDataCompare(org.bukkit.inventory.ItemStack i, short data) {
        return i.getData().getData() == data;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void setJoinSignBackgroundBlockData(org.bukkit.block.BlockState block, byte data) {
        block.getBlock().getRelative(((org.bukkit.material.Sign)block.getData()).getAttachedFace()).setData(data, true);
    }

    @Override
    public org.bukkit.Material woolMaterial() {
        return org.bukkit.Material.WOOL;
    }

    @Override
    public String getShopUpgradeIdentifier(org.bukkit.inventory.ItemStack itemStack) {
        ItemStack i = CraftItemStack.asNMSCopy(itemStack);
        NBTTagCompound tag = i.getTag();
        return tag == null ? "" : tag.hasKey("tierIdentifier") ? tag.getString("tierIdentifier") : "";
    }

    @Override
    public org.bukkit.inventory.ItemStack setShopUpgradeIdentifier(org.bukkit.inventory.ItemStack itemStack, String identifier) {
        ItemStack i = CraftItemStack.asNMSCopy(itemStack);
        NBTTagCompound tag = i.getTag();
        if (tag == null) {
            tag = new NBTTagCompound();
            i.setTag(tag);
        }
        tag.setString("tierIdentifier", identifier);
        return CraftItemStack.asBukkitCopy(i);
    }

    @Override
    public org.bukkit.inventory.ItemStack getPlayerHead(Player player) {
        org.bukkit.inventory.ItemStack head = new org.bukkit.inventory.ItemStack(org.bukkit.Material.SKULL_ITEM, 1, (short)3);

        SkullMeta headMeta = (SkullMeta) head.getItemMeta();
        Field profileField;
        try {
            profileField = headMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(headMeta, ((CraftPlayer)player).getProfile());
        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e1) {
            e1.printStackTrace();
        }
        head.setItemMeta(headMeta);
        return head;
    }

    @Override
    public void invisibilityFix(Player player, IArena arena) {
        EntityPlayer pc = ((CraftPlayer) player).getHandle();

        for (Player pl : arena.getPlayers()){
            if (pl.equals(player)) continue;
            if (arena.getRespawn().containsKey(pl)) continue;
            if (arena.getShowTime().containsKey(pl)) continue;
            if (pl.getLocation().distanceSquared(player.getLocation()) <= renderDistance) {
                pc.playerConnection.sendPacket(new PacketPlayOutNamedEntitySpawn(((CraftPlayer) pl).getHandle()));
                pc.playerConnection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, ((CraftPlayer) pl).getHandle()));
                showArmor(pl, player);
            }
        }
    }

    @Override
    public String getInventoryName(InventoryEvent e) {
        return e.getInventory().getName();
    }

    @Override
    public void setUnbreakable(ItemMeta itemMeta) {
        itemMeta.spigot().setUnbreakable(true);
    }

    @Override
    public String getMainLevel() {
        return ((DedicatedServer) MinecraftServer.getServer()).propertyManager.properties.getProperty("level-name");
    }

    @Override
    public int getVersion() {
        return 2;
    }

    @Override
    public void registerVersionListeners() {
        new VersionCommon(this);
    }

    @Override
    public void setJoinSignBackground(org.bukkit.block.BlockState b, org.bukkit.Material material) {
        b.getLocation().getBlock().getRelative(((org.bukkit.material.Sign)b.getData()).getAttachedFace()).setType(material);
    }
}
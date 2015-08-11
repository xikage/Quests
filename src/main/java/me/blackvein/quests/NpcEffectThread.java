package me.blackvein.quests;

import java.util.List;

import net.citizensnpcs.api.npc.NPC;
import net.dmulloy2.quests.packets.WrapperPlayServerWorldParticles;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.wrappers.EnumWrappers.Particle;

public class NpcEffectThread implements Runnable {
    private final Quests plugin;

    public NpcEffectThread(Quests quests) {
        plugin = quests;
    }

	@Override
    public void run() {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            Quester quester = plugin.getQuester(player.getUniqueId());
            List<Entity> nearby = player.getNearbyEntities(32.0, 32.0, 32.0);
            if (nearby.isEmpty() == false) {
                for (Entity e : nearby) {
                    if (plugin.citizens != null) {
                        if (plugin.citizens.getNPCRegistry().isNPC(e)) {
                            NPC npc = plugin.citizens.getNPCRegistry().getNPC(e);
                            if (plugin.hasQuest(npc, quester)) {
                                showEffect(player, npc);
                            }
                        }
                    }
                }
            }
        }
    }

	private ProtocolManager manager;

    private void showEffect(Player player, NPC npc) {
        if (manager == null) {
            manager = ProtocolLibrary.getProtocolManager();
        }

        Location eyeLoc = npc.getEntity().getLocation();
        eyeLoc.setY(eyeLoc.getY() + 1.5);

        WrapperPlayServerWorldParticles packet = new WrapperPlayServerWorldParticles();

        // Defaults
        packet.setX((float) eyeLoc.getX());
        packet.setY((float) eyeLoc.getY());
        packet.setZ((float) eyeLoc.getZ());
        packet.setOffsetX(0);
        packet.setOffsetY(0);
        packet.setOffsetZ(0);
        packet.setParticleData(1);
        packet.setNumberOfParticles(0);
        packet.setData(null);

        // Specifics for each particle
        switch (Quests.effect.toLowerCase()) {
            case "enchant":
                packet.setParticleType(Particle.ENCHANTMENT_TABLE);
                packet.setOffsetY(1);
                packet.setNumberOfParticles(10);
                break;
            case "crit":
                packet.setParticleType(Particle.CRIT);
                packet.setParticleData(0.35f);
                packet.setNumberOfParticles(3);
                break;
            case "spell":
                packet.setParticleType(Particle.SPELL_INSTANT);
                packet.setNumberOfParticles(3);
                break;
            case "magiccrit":
                packet.setParticleType(Particle.CRIT_MAGIC);
                packet.setParticleData(0.35f);
                packet.setNumberOfParticles(3);
                break;
            case "mobspell":
                packet.setParticleType(Particle.SPELL_MOB);
                packet.setNumberOfParticles(3);
                break;
            case "note":
                packet.setParticleType(Particle.NOTE);
                packet.setY((float) eyeLoc.getY() + 0.5f);
                packet.setNumberOfParticles(1);
                break;
            case "portal":
                packet.setParticleType(Particle.PORTAL);
                packet.setNumberOfParticles(5);
                break;
            case "dust":
                packet.setParticleType(Particle.REDSTONE);
                packet.setY((float) eyeLoc.getY() + 0.5f);
                packet.setNumberOfParticles(1);
                break;
            case "witch":
                packet.setParticleType(Particle.SPELL_WITCH);
                packet.setNumberOfParticles(3);
                break;
            case "snowball":
                packet.setParticleType(Particle.SNOWBALL);
                packet.setY((float) eyeLoc.getY() + 0.5f);
                packet.setNumberOfParticles(3);
                break;
            case "splash":
                packet.setParticleType(Particle.WATER_SPLASH);
                packet.setY((float) eyeLoc.getY() + 0.5f);
                packet.setNumberOfParticles(4);
                break;
            case "smoke":
                packet.setParticleType(Particle.TOWN_AURA);
                packet.setOffsetY(1);
                packet.setNumberOfParticles(20);
                break;
            default:
                Particle particle = Particle.getByName(Quests.effect);
                if (particle == null) {
                    Quests.getInstance().getLogger().warning(Quests.effect + " is not a valid effect name.");
                    return;
                }

                packet.setParticleType(particle);
                packet.setNumberOfParticles(3);
                break;
        }

        packet.sendPacket(player);
    }
}
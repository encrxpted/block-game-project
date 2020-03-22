package main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

/*
 * Listens for a mouse click and allows the player to place a 
 * particle where they clicked
 */
public class ClickListener implements Listener {
	private final int COOLDOWN_MS = 1000;

	private int count; // counts # of vertices placed

	// Will store the locations of the <= 3 particles that we might draw a triangle from (vertices)
	private List<Location> particleLocs = new ArrayList<Location>();

	// Stores the time that each player last spawned a particle <player name, last time>
	private HashMap<String, Integer> cooldowns = new HashMap<String, Integer>();

	// Stores locations of particles on each side of the triangle when it is drawn
	private List<Location> side1 = new ArrayList<Location>();
	private List<Location> side2 = new ArrayList<Location>();
	private List<Location> side3 = new ArrayList<Location>();

	public ClickListener() {
		count = 0;
	}

	@EventHandler
	public void onPlayerRightClicked(PlayerInteractEvent e) {

		// Spawns in a particle on the player's location if the player has right clicked
		Player p = e.getPlayer();
		if ((e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)
				&& canPlayerSpawnParticles(p.getName())) {

			p.spawnParticle(Particle.HEART, p.getLocation(), 5);
			System.out.println("spawned particle");
			count++;
			particleLocs.add(p.getLocation());

			cooldowns.put(p.getName(), (int) System.currentTimeMillis()); // log the time the player spawned particle
		}

		// If this is the 3rd particle, then draw the triangle
		// Idk why but this thing would get triggered 2x each time I right clicked, so I just put
		// 6 instead of 3 here to compensate...
		if (count == 6) {

			// Draws sides of the triangle
			side1 = drawLine(particleLocs.get(0), particleLocs.get(2), p); // draw lines of particles between vertices
			side2 = drawLine(particleLocs.get(0), particleLocs.get(4), p);
			side3 = drawLine(particleLocs.get(2), particleLocs.get(4), p);
			drawLine(particleLocs.get(2), particleLocs.get(0), p);
			drawLine(particleLocs.get(4), particleLocs.get(0), p);
			drawLine(particleLocs.get(4), particleLocs.get(2), p);
			p.spawnParticle(Particle.HEART, particleLocs.get(0), 5); // spawn particles at the vertices
			p.spawnParticle(Particle.HEART, particleLocs.get(2), 5);
			p.spawnParticle(Particle.HEART, particleLocs.get(4), 5);

			// if "fill" is true, then we need to fill in the triangle
			// not perfect, but I tried :C
			if (ToggleFillCommand.isFill()) {
				fillBetweenSides(side1, side2, p);
				fillBetweenSides(side1, side3, p);
				fillBetweenSides(side3, side2, p);
			}

			// Clears list and resets count value after triangle is made
			particleLocs.clear();
			count = 0;
			side1.clear();
			side2.clear();
			side3.clear();

		}
	}

	// draws particles between the locations of each side (i tried :c)
	private void fillBetweenSides(List<Location> s1, List<Location> s2, Player p) {

		// if s1 is bigger than s2, swap them
		if (s1.size() > s2.size()) {
			List<Location> temp = new ArrayList<Location>();
			for (Location l : s1)
				temp.add(l);
			s1.clear();
			for (Location l : s2)
				s1.add(l);
			s2.clear();
			for (Location l : temp)
				s2.add(l);
		}

		// we know for sure s1's size < s2's size now

		// draw lines of particles between the particles on each of the 2 sides to appear as a fill
		for (int i = 0; i < s1.size(); i++) {
			drawLine(s1.get(i), s2.get(i), p);
			drawLine(s1.get(s1.size() - 1 - i), s2.get(s2.size() - 1 - i), p);
			drawLine(s1.get(i), s2.get(s2.size() - 1 - i), p);
			drawLine(s1.get(s1.size() - 1 - i), s2.get(i), p);
		}

	}

	// Returns if the player is on cooldown for spawning particles or not
	private boolean canPlayerSpawnParticles(String name) {
		if (!cooldowns.containsKey(name)) {
			System.out.println("cooldowns didn't contain player name");
			return true; // return true if player isn't in the hashmap
		}

		// checks if the duration between now and the time logged in the hashmap is
		// greater than the cooldown duration
		long duration = System.currentTimeMillis() - cooldowns.get(name);
		if (duration > COOLDOWN_MS) {
			// System.out.println("Duration " + duration);
			return true;
		} 
		else
			return false;
	}

	// Draws a line of particles between the vectors a and b
	// Returns a List of the locations of the particles drawn
	private List<Location> drawLine(Location first, Location second, Player p) {
		// this is to avoid that weird java thing where passing an object as a parameter
		// affects the original object...
		Location loc1 = first.clone();
		Location loc2 = second.clone();
		List<Location> particles = new ArrayList<Location>();

		Vector vector = getDirectionBetweenLocations(loc1, loc2); // vector between loc1 and loc2
		for (double i = 1; i <= loc1.distance(loc2); i += 0.5) { // each iteration draws a particle
			vector.normalize(); 
			vector.multiply(i);
			loc1.add(vector);
			p.spawnParticle(Particle.HEART, loc1, 5); // spawns particle that is distance of i away from original
														// loc1, toward the direction of loc2
			particles.add(loc1.clone());
			loc1.subtract(vector); // change loc1 back to what it was originally for next iteration
		}
		return particles;
	}

	// returns vector between the locations
	private Vector getDirectionBetweenLocations(Location Start, Location End) {
		Vector from = Start.toVector();
		Vector to = End.toVector();
		return to.subtract(from);
	}

}

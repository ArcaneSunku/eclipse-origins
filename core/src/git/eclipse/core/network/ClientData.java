package git.eclipse.core.network;

import org.joml.Vector2f;

import java.net.InetAddress;

/**
 * <p>Data containing class. Less a class, more a struct. Obviously don't exist in java, so.</p>
 */
public class ClientData {

    /**
     * <p>List of Privilege levels that are related to what client functions are available to the user.</p>
     *  ie:
     *  <ul>
     *  <li>PLAYER - Simple Game functions, that's it!</li>
     *  <li>MODERATOR - They get to watch over the players, they can temp-ban/mute people; look at them go!</li>
     *  <li>ADMIN - Above the moderators, but do the same can perma-ban and unban people.</li>
     *  <li>MAPPER - Someone that has access to the built-in map editor(soon). (No moderation tools!)</li>
     *  <li>DESIGNER - Someone that creates the core content of the game with the other editors at hand. Can edit maps too! (No moderation tools!)</li>
     *  <li>OWNER - Self explanatory, can do literally everything.</li>
     *  </ul>
     */
    public enum Privilege {
        PLAYER,
        MODERATOR, ADMIN,
        MAPPER, DESIGNER,
        OWNER
    }

    public String IP = "localhost";
    public int Port = 7001;

    public String UserName = "UserName";
    public String Password = "PassWord";

    public Privilege PermLevel = Privilege.PLAYER;

    public Vector2f Position = new Vector2f(0.0f, 0.0f);
    public Vector2f Scale = new Vector2f(1.0f, 1.0f);

    // TODO: Add data for Inventory/Equipment/Class/Skills/etc

}

package me.Barni;


public class Collectable extends Entity {

    private boolean collected;

    public Collectable(Game g, String name, Vec2D pos) {
        super(g, name, pos);
        size.x = 32;
        size.y = 32;
        colliderHitbox.x = position.xi();
        colliderHitbox.y = position.yi();
        colliderHitbox.w = size.xi();
        colliderHitbox.h = size.yi();
        loadTexture("plasma_tank");
    }

    @Override
    public void onTouch(Entity other) {
        if (collected) return;
        if (other.name.equals("player")) {
            collected = true;
            Player p = (Player) other;
            p.setLevel(p.getLevel() + 1);
            visible = false;
            HUDNotification n = (HUDNotification) game.hud.root.getElement("PlayerNotification");
            n.message = "Player level ugraded (" + p.getLevel() + "/3)";
            n.show(220);
        }
    }
}

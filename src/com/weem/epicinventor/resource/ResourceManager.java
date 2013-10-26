package com.weem.epicinventor.resource;

import com.weem.epicinventor.*;
import com.weem.epicinventor.actor.*;
import com.weem.epicinventor.drop.*;
import com.weem.epicinventor.network.*;
import com.weem.epicinventor.utility.*;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.ArrayList;

public class ResourceManager extends Manager implements Serializable {
    
    protected static final long serialVersionUID = 10000L;
    private HashMap<String, Resource> resources;
    private ArrayList<ResourceType> resourceTypes;
    private final static String CONFIG_FILE = "Resources.dat";
    
    public ResourceManager(GameController gc, Registry rg) {
        super(gc, rg);
        
        resources = new HashMap<String, Resource>();
        
        resourceTypes = new ArrayList<ResourceType>();
        
        loadResourceTypes("Resources.dat");
        
        checkResources();
    }
    
    @Override
    public void setTransient(Registry rg) {
        super.setTransient(rg);
        
        try {
            for (String key : resources.keySet()) {
                Resource resource = (Resource) resources.get(key);
                resource.setTransient(rg, this);
            }
        } catch (ConcurrentModificationException concEx) {
            //another thread was trying to modify resources while iterating
            //we'll continue and the new item can be grabbed on the next update
        }
        
        for (int i = 0; i < resourceTypes.size(); i++) {
            resourceTypes.get(i).setTransient(rg);
        }
    }
    
    public void registerResource(Resource r) {
        if (!resources.containsKey(r.getId())) {
            resources.put(r.getId(), r);
        }
    }
    
    private void loadResourceTypes(String fn) {
        String line;
        String parts[];
        
        try {
            InputStream in = getClass().getResourceAsStream(GameController.CONFIG_DIR + fn);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            
            int mapLevel = 1;
            String name = "";
            String type = "";
            int qtyMin = 0;
            int qtyMax = 0;
            int gatherTime = 0;
            int[] levels;
            boolean isHeader = true;
            
            ResourceType rt;
            levels = new int[5];
            
            while ((line = br.readLine()) != null) {
                if (line.length() == 0) {
                    continue;
                }
                if (line.startsWith("//")) {
                    continue;
                }
                
                if (isHeader) {
                    parts = line.split(" ");
                    if (parts.length != 5) {
                        System.out.println("Error in " + fn + " (" + parts.length + ")");
                    }
                    
                    name = parts[0];
                    type = parts[1];
                    qtyMin = Integer.parseInt(parts[2]);
                    qtyMax = Integer.parseInt(parts[3]);
                    gatherTime = Integer.parseInt(parts[4]);
                    
                    isHeader = false;
                    mapLevel = 0;
                    levels = new int[5];
                } else {
                    levels[mapLevel] = Integer.parseInt(line);
                    
                    if (mapLevel >= 4) {
                        rt = new ResourceType(this, registry, name, type, qtyMin, qtyMax, gatherTime, levels);
                        resourceTypes.add(rt);
                        isHeader = true;
                    }
                    mapLevel++;
                }
            }
            in.close();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
    
    public ResourceType getResourceTypeByName(String name) {
        ResourceType resourceType = null;
        
        for (int i = 0; i < resourceTypes.size(); i++) {
            resourceType = resourceTypes.get(i);
            if (resourceType.getName().equals(name)) {
                return resourceType;
            }
        }
        
        return resourceType;
    }
    
    private void checkResources() {
        int level = 0;
        int needed = 0;
        int[] levels;
        int[] resourcesInLevels;
        Resource resource = null;
        ResourceType resourceType = null;
        
        for (int i = 0; i < resourceTypes.size(); i++) {
            resourceType = resourceTypes.get(i);
            levels = resourceType.getLevels();
            resourcesInLevels = new int[5];
            
            try {
                for (String key : resources.keySet()) {
                    resource = (Resource) resources.get(key);
                    if (resource.getResourceType() == resourceType) {
                        level = registry.getBlockManager().getLevelByY(resource.getMapY());
                        if (level >= 0 && level < levels.length) {
                            resourcesInLevels[level]++;
                        }
                    }
                }
            } catch (ConcurrentModificationException concEx) {
                //another thread was trying to modify resources while iterating
                //we'll continue and the new item can be grabbed on the next update
            }
            
            for (int x = 0; x < 5; x++) {
                needed = levels[x] - resourcesInLevels[x];
                if (needed > 0) {
                    spawnResources(resourceType, needed, x);
                }
            }
        }
    }
    
    private void spawnResources(ResourceType rt, int count, int level) {
        Point p;
        int y;
        for (int i = 0; i < count; i++) {
            p = rt.getNewXY(gameController.getMapWidth(), level);
            Resource r = new Resource(registry, this, rt, p.x, p.y, 0);
            y = findNextFloor(p.x + r.getWidth(), p.y, r.getHeight());
            if (p.y == y) {
                //make sure the resource isn't spawned in view
                if (this.isInPlayerView(p) || this.isInFrontOfPlaceable(r.getPerimeter())) {
                    r = null;
                } else {
                    registerResource(r);
                    if (gameController.multiplayerMode == gameController.multiplayerMode.SERVER && registry.getNetworkThread() != null) {
                        if (registry.getNetworkThread().readyForUpdates) {
                            registry.getNetworkThread().sendData(r);
                        }
                    }
                }
            }
        }
    }
    
    public void spawnXPCrystal(int x, int y, int xp) {
        Resource r = new Resource(registry, this, this.getResourceTypeByName("XPCrystal"), x, y, xp);
        r.setMapY(findNextFloor(x + r.getWidth(), y, r.getHeight()));
        
        registerResource(r);
        if (gameController.multiplayerMode == gameController.multiplayerMode.SERVER && registry.getNetworkThread() != null) {
            if (registry.getNetworkThread().readyForUpdates) {
                registry.getNetworkThread().sendData(r);
            }
        }
    }
    
    @Override
    public ResourceType getResourceTypeByResourceId(String id) {
        if (resources.containsKey(id)) {
            Resource resource = resources.get(id);
            return resource.getResourceType();
        } else {
            return null;
        }
    }
    
    @Override
    public Resource getResourceById(String id) {
        if (resources.containsKey(id)) {
            Resource resource = resources.get(id);
            return resource;
        } else {
            return null;
        }
    }
    
    public void stopGather() {
        Resource resource = null;
        try {
            for (String key : resources.keySet()) {
                resource = (Resource) resources.get(key);
                resource.setCollecting(null, false);
            }
        } catch (ConcurrentModificationException concEx) {
            //another thread was trying to modify resources while iterating
            //we'll continue and the new item can be grabbed on the next update
        }
    }
    
    public String startGather(Player player, Point p, int maxDistance) {
        Resource resource = null;
        Resource closestResource = null;
        
        double closestDistance = 0;
        
        try {
            for (String key : resources.keySet()) {
                resource = (Resource) resources.get(key);
                resource.setCollecting(player, false);
                if (p.distance(resource.getCenterPoint()) < closestDistance || closestDistance == 0) {
                    closestResource = resource;
                    closestDistance = p.distance(resource.getCenterPoint());
                }
            }
        } catch (ConcurrentModificationException concEx) {
            //another thread was trying to modify resources while iterating
            //we'll continue and the new item can be grabbed on the next update
        }
        
        if (closestResource != null && closestDistance <= maxDistance) {
            closestResource.setCollecting(player, true);
            return closestResource.getId();
        }
        
        return "";
    }
    
    public void resourceDoneCollecting(Resource r) {
        Player p = registry.getPlayerManager().getCurrentPlayer();
        resourceDoneCollecting(p, r);
    }
    
    public void resourceDoneCollecting(Player p, Resource r) {
        int qty = r.getResourceType().getQty();
        
        gameController.stopGather();
        
        if (gameController.multiplayerMode == gameController.multiplayerMode.CLIENT && registry.getNetworkThread() != null) {
            if (registry.getNetworkThread().readyForUpdates) {
                UpdatePlayer up = new UpdatePlayer(p.getId());
                up.mapX = p.getMapX();
                up.mapY = p.getMapY();
                up.vertMoveMode = p.getVertMoveMode();
                up.action = "CollectedResource";
                up.dataString = r.getId();
                registry.getNetworkThread().sendData(up);
            }
        } else {
            if (r.getXP() > 0) {
                SoundClip cl = new SoundClip("Player/Good");
                p.addXP(r.getXP());
                r.destroy();
                if (gameController.multiplayerMode == gameController.multiplayerMode.SERVER && registry.getNetworkThread() != null) {
                    if (registry.getNetworkThread().readyForUpdates) {
                        UpdateResource ur = new UpdateResource(r.getId());
                        ur.action = "Destroy";
                        registry.getNetworkThread().sendData(ur);
                    }
                }
            } else {
                if (gameController.playerAddItem(p, r.getResourceType().getName(), qty) == 0
                        || (r.getResourceType().getQtyMin() == 0
                        && r.getResourceType().getQtyMax() == 0)) {
                    
                    ArrayList<Drop> drops = new ArrayList<Drop>();
                    
                    if (r.getResourceType().getName().equals("Stone")) {
                        drops.add(new Drop(r.getResourceType().getName(), qty));
                        if (Rand.getRange(1, 100) <= 30) {
                            gameController.playerAddItem(p, "Pebble", Rand.getRange(1, 2));
                            drops.add(new Drop("Pebble", 1));
                        }
                    } else if (r.getResourceType().getName().equals("Wood")) {
                        drops.add(new Drop(r.getResourceType().getName(), qty));
                        if (Rand.getRange(1, 100) <= 20) {
                            gameController.playerAddItem(p, "Thorn", Rand.getRange(1, 1));
                            drops.add(new Drop("Thorn", 1));
                        }
                        if (Rand.getRange(1, 100) <= 15) {
                            gameController.playerAddItem(p, "Flower", Rand.getRange(1, 1));
                            drops.add(new Drop("Flower", 1));
                        }
                    } else if (r.getResourceType().getName().equals("Plant")) {
                        if (Rand.getRange(1, 100) <= 40) {
                            gameController.playerAddItem(p, "Thorn", Rand.getRange(1, 2));
                            drops.add(new Drop("Thorn", 1));
                        }
                        if (Rand.getRange(1, 100) <= 25) {
                            gameController.playerAddItem(p, "Web", Rand.getRange(1, 1));
                            drops.add(new Drop("Web", 1));
                        }
                        if (Rand.getRange(1, 100) <= 30) {
                            gameController.playerAddItem(p, "Flower", Rand.getRange(1, 1));
                            drops.add(new Drop("Flower", 1));
                        }
                        if (Rand.getRange(1, 100) <= 2) {
                            gameController.playerAddItem(p, "PumpkinSeed", Rand.getRange(1, 2));
                            drops.add(new Drop("PumpkinSeed", 1));
                        }
                        if (Rand.getRange(1, 100) <= 5) {
                            gameController.playerAddItem(p, "WheatSeed", Rand.getRange(1, 2));
                            drops.add(new Drop("WheatSeed", 1));
                        }
                    } else {
                        drops.add(new Drop(r.getResourceType().getName(), qty));
                    }
                    registry.getIndicatorManager().createIndicator(registry.getPlayerManager().getCurrentPlayer().getMapX() + (registry.getPlayerManager().getCurrentPlayer().getWidth() / 2), registry.getPlayerManager().getCurrentPlayer().getMapY() + 32, drops);
                    
                    r.destroy();
                    if (gameController.multiplayerMode == gameController.multiplayerMode.SERVER && registry.getNetworkThread() != null) {
                        if (registry.getNetworkThread().readyForUpdates) {
                            UpdateResource ur = new UpdateResource(r.getId());
                            ur.action = "Destroy";
                            registry.getNetworkThread().sendData(ur);
                        }
                    }
                    if (registry.getPlayerManager().getCurrentPlayer() == p) {
                        SoundClip cl = new SoundClip("Player/Good");
                    }
                }
            }
        }
    }
    
    @Override
    public void update() {
        super.update();
        
        boolean resourcesUpdated = false;
        Resource resource = null;
        ArrayList dirtyResources = new ArrayList();
        
        try {
            for (String key : resources.keySet()) {
                resource = (Resource) resources.get(key);
                if (resource != null) {
                    resource.update();
                    
                    if (resource.isDirty()) {
                        dirtyResources.add(key);
                    }
                }
            }
        } catch (ConcurrentModificationException concEx) {
            //another thread was trying to modify resources while iterating
            //we'll continue and the new item can be grabbed on the next update
        }
        
        if (dirtyResources.size() > 0) {
            resourcesUpdated = true;
            for (int i = 0; i < dirtyResources.size(); i++) {
                resources.remove((String) dirtyResources.get(i));
            }
        }
        
        if (resourcesUpdated && gameController.multiplayerMode != gameController.multiplayerMode.CLIENT) {
            checkResources();
        }
    }
    
    public void render(Graphics g) {
        Resource resource = null;
        
        try {
            for (String key : resources.keySet()) {
                resource = (Resource) resources.get(key);
                resource.render(g);
            }
        } catch (ConcurrentModificationException concEx) {
            //another thread was trying to modify resources while iterating
            //we'll continue and the new item can be grabbed on the next update
        }
    }
    
    public void processResourceUpdate(UpdateResource ur) {
        if (ur != null) {
            if (resources.containsKey(ur.id)) {
                Resource resource = resources.get(ur.id);
                if (resource != null) {
                    EIError.debugMsg(ur.id + " (" + ur.action + ")");
                    if (ur.action.equals("Destroy")) {
                        resource.destroy();
                    }
                }
            }
        }
    }
    
    private void readObject(ObjectInputStream aInputStream) throws Exception {
        aInputStream.defaultReadObject();
    }
    
    private void writeObject(ObjectOutputStream aOutputStream) throws Exception {
        aOutputStream.defaultWriteObject();
    }
}

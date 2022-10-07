package model;

import solids.ISolid;

import java.util.ArrayList;

public class Scene {
    private ArrayList<ISolid> solids = new ArrayList<>();

    public void add(ISolid solid){
        solids.add(solid);
    }

    public ArrayList<ISolid> getSolids() {
        return solids;
    }
}

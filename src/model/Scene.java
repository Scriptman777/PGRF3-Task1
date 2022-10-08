package model;

import solids.IRenderable;

import java.util.ArrayList;

public class Scene {
    private ArrayList<IRenderable> solids = new ArrayList<>();

    public void add(IRenderable solid){
        solids.add(solid);
    }

    public ArrayList<IRenderable> getSolids() {
        return solids;
    }
}

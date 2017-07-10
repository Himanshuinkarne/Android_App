package com.svc.sml;

import com.svc.sml.Model.BaseAccessoryItem;
import com.svc.sml.Utility.ConstantsUtil;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by himanshu on 5/13/16.
 */
public class MixMatchSharedResource {
    public String renderedHairStyle ="";
    public String renderedSpecs = "";
    public BaseAccessoryItem currentSelectedItem = null;
    public Set<String> listRenderedAccessory = new HashSet<>();

    private static MixMatchSharedResource ourInstance = new MixMatchSharedResource();

    public static MixMatchSharedResource getInstance() {
        return ourInstance;
    }


    private MixMatchSharedResource() {

    }

    public void addAccessory(String accessoryType){
        if(accessoryType.equals(ConstantsUtil.EAccessoryType.eAccTypeSpecs.toString())
                || accessoryType.equals(ConstantsUtil.EAccessoryType.eAccTypeSunglasses.toString()) )
              {
            listRenderedAccessory.add(ConstantsUtil.EAccessoryType.eAccTypeSpecs.toString());
            listRenderedAccessory.add(ConstantsUtil.EAccessoryType.eAccTypeSunglasses.toString());
        }
        else if(accessoryType.equals(ConstantsUtil.EAccessoryType.eAccTypeBags.toString())
                ||accessoryType.equals(ConstantsUtil.EAccessoryType.eAccTypeClutches.toString())){
            listRenderedAccessory.add(ConstantsUtil.EAccessoryType.eAccTypeBags.toString());
            listRenderedAccessory.add(ConstantsUtil.EAccessoryType.eAccTypeClutches.toString());
        }
        else {
            listRenderedAccessory.add(accessoryType);
        }
    }
    public void addAccessory(String accessoryType, String accessoryId){
        /* For Default */
        if(accessoryType.equals(ConstantsUtil.EAccessoryType.eAccTypeSpecs.toString())){
            renderedSpecs = accessoryId;
        }
        else if(accessoryType.equals(ConstantsUtil.EAccessoryType.eAccTypeHair.toString())){
            renderedHairStyle = accessoryId;
        }
        addAccessory(accessoryType);
    }

    public void addAccessory(BaseAccessoryItem item){
       currentSelectedItem = item;
        addAccessory(item.getAccessoryType(), item.getObjId());
    }

    private void removeAccessoryForDefault(String accessoryType){
        if(accessoryType.equals(ConstantsUtil.EAccessoryType.eAccTypeSpecs.toString())
                || accessoryType.equals(ConstantsUtil.EAccessoryType.eAccTypeSunglasses.toString())){
             renderedSpecs = "";
        }
    }

    public void removeAccessory(String accessoryType){
        currentSelectedItem = null;
        /* Hair and Shoes not to be removed */
        removeAccessoryForDefault(accessoryType);
         if(accessoryType.equals(ConstantsUtil.EAccessoryType.eAccTypeSpecs.toString())
                ||accessoryType.equals(ConstantsUtil.EAccessoryType.eAccTypeSunglasses.toString())){
             listRenderedAccessory.remove(ConstantsUtil.EAccessoryType.eAccTypeSpecs.toString());
             listRenderedAccessory.remove(ConstantsUtil.EAccessoryType.eAccTypeSunglasses.toString());
        }
        else if(accessoryType.equals(ConstantsUtil.EAccessoryType.eAccTypeBags.toString())
                ||accessoryType.equals(ConstantsUtil.EAccessoryType.eAccTypeClutches.toString())){
            listRenderedAccessory.remove(ConstantsUtil.EAccessoryType.eAccTypeBags.toString());
            listRenderedAccessory.remove(ConstantsUtil.EAccessoryType.eAccTypeClutches.toString());
        }
        else {
            listRenderedAccessory.remove(accessoryType);
        }
    }

    public void reset(){
        listRenderedAccessory.clear();
        renderedHairStyle ="";
        renderedSpecs = "";
        currentSelectedItem = null;
    }

    public boolean isAccessoryTypeRendered(String accessoryType){
        return listRenderedAccessory.contains(accessoryType);
    }

}

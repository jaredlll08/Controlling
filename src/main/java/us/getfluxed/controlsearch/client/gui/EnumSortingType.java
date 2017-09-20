package us.getfluxed.controlsearch.client.gui;

public enum EnumSortingType {
    
    DEFAULT, AZ, ZA;
    
    
    public EnumSortingType cycle() {
        switch(this) {
            case DEFAULT:
                return AZ;
            case AZ:
                return ZA;
            case ZA:
                return DEFAULT;
            default:
                return DEFAULT;
        }
    }
    
    public String getName(){
        switch(this) {
            case DEFAULT:
                return "Default";
            case AZ:
                return "A->Z";
            case ZA:
                return "Z->A";
            default:
                return "Default";
        }
    }
    
    
}

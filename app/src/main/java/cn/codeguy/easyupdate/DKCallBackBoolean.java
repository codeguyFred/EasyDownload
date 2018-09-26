package cn.codeguy.easyupdate;

/**
 * 具备判断条件的回调
 */
public interface DKCallBackBoolean {
    int YES=1;
    int NO=0;
    void action(int type);
}
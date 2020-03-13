package org.xxpay.shop.enums;

/**
 * @description: DeliverStatusEnum 24小时机器出货状态枚举
 * @author: aoxiang
 * @create: 2020/03/04 11:19
 **/
//public class DeliverStatusEnum {
public enum DeliverStatusEnum {
    OK("出货成功", 0), FAIL_STOCK("出货失败,售货机库存不足", 2), FAIL_OUT("出货失败", 3),
    FAIL_NET("出货失败,网络原因", 4), FAIL_TOMEOUT("出货失败,支付超时,超过了支付等待时间", 5);
    //成员变量
    private String name;
    private int index;

    //构造方法
    private DeliverStatusEnum(String name, int index) {
        this.name = name;
        this.index = index;
    }

    //覆盖方法
    @Override
    public String toString() {
//        return this.index + "-" + this.name;
        return this.name;
    }

    public int getIndex() {
        return this.index;
    }

}

class test {
    public static void main(String[] args) {

        for (DeliverStatusEnum color : DeliverStatusEnum.values()) {
            if (color.getIndex() == 0) {
                System.out.println(color.toString());
            }
        }
    }
}


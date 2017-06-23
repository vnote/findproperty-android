package com.cetnaline.findproperty.entity.ui;

/**
 * Created by diaoqf on 2016/8/15.
 */
public class LoanCalculateBo {
    public int modeType;//0:贷款总额;1:按揭;2:组合
    public int loanType;//0:等额本息;1:等额本金
    public int month;//还款月数
    public double totalPrice;//房屋总价
    public double businessPrice;//商业贷款金额
    public double fundPrice;//公积金贷款金额
    public double ratio;//按揭成数;
    public double businessRate;//商贷利率
    public double fundRate;//公积金利率
}

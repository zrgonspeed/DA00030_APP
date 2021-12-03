package com.bike.ftms.app.bean.rundata.put;

import com.bike.ftms.app.bean.rundata.RowerDataBean1;
import com.bike.ftms.app.bean.rundata.RowerDataBean2;
import com.bike.ftms.app.utils.TimeStringUtil;

import java.util.ArrayList;
import java.util.List;

/*
{
    "date": "2021-10-20 10:07:00",
    "type": "100C",
    "result": "73M",
    "totals": {
        "time": "00:01:40",
        "meters": "500M",
        "efm": "01:35",
        "cals": "44",
        "sm": "73",
        "calhr": "1686",
        "watts": "403"
    },
    "items": [
        {
            "time": "00:00:20",
            "meters": "100M",
            "efm": "01:35",
            "cals": "9",
            "sm": "73",
            "calhr": "1686",
            "watts": "403"
        },
        ...
    ]
}
 */
public class RunDataBO {
    private String date;
    private String type;
    private String result;

    private RunDataItemBO totals;
    private List<RunDataItemBO> items = new ArrayList<>();

    public RunDataBO() {
    }

    public RunDataBO(RowerDataBean1 bean1) {
        toSelf(bean1);
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public RunDataItemBO getTotals() {
        return totals;
    }

    public void setTotals(RunDataItemBO totals) {
        this.totals = totals;
    }

    public List<RunDataItemBO> getItems() {
        return items;
    }

    public void setItems(List<RunDataItemBO> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "RunDataBO{" +
                "date='" + date + '\'' +
                ", type='" + type + '\'' +
                ", result='" + result + '\'' +
                ", totals=" + totals +
                ", items=" + items +
                '}';
    }

    /**
     * 上传到服务器的数据对象设置
     *
     * @param bean1
     */
    private void toSelf(RowerDataBean1 bean1) {
        setDate(TimeStringUtil.getDate2String(bean1.getDate(), "yyyy-MM-dd HH:mm:ss"));

        bean1.setTypeAndResult();
        setType(bean1.getType());
        setResult(bean1.getResult());

        // 1.bean1 先计算出 总结item
        // 2.bean1.getItem  -> itemBO
        bean1.setTotalsItem();
        RowerDataBean2 totalsItem = bean1.getTotalsItem();
        RunDataItemBO totals = new RunDataItemBO(totalsItem);
        setTotals(totals);

        // 设置各详细item
        List<RunDataItemBO> items = new ArrayList<>();
        List<RowerDataBean2> list = bean1.getList();
        for (RowerDataBean2 bean2 : list) {
            items.add(new RunDataItemBO(bean2));
        }
        setItems(items);
    }
}

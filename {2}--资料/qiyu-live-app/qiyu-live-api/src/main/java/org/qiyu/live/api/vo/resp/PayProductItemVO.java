package org.qiyu.live.api.vo.resp;

/**
 * @Author idea
 * @Date: Created in 08:31 2023/8/17
 * @Description
 */
public class PayProductItemVO {

    private Long id;

    private String name;

    private Integer coinNum;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCoinNum() {
        return coinNum;
    }

    public void setCoinNum(Integer coinNum) {
        this.coinNum = coinNum;
    }

    @Override
    public String toString() {
        return "PayProductVO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", coinNum=" + coinNum +
                '}';
    }
}

package com.yahh.bPlusdis.sds;

import lombok.Data;

/**
 * @author 邹磊
 * @version 1.0
 * @description:
 * @date 2021/3/8 19:16
 */
@Data
public class SimDynamicStr {

    /**
     *  容量 这里的int以后可以改成泛型
     */
    Integer capacity;

    Integer len;

    private byte flags;

    /**
     * 数组内容
     */
    private byte[] content;






}

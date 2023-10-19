package com.jokingsun.oilfairy.base.callback;

import java.util.List;

/**
 * @author cfd058
 */
public interface iBaseRecyclerViewAdapter<B> {

    /**
     * CRUD Methods
     */
    void set(int position, B data);

    void remove(int pos);

    void setData(List<B> dataList);

    void add(int position, B data);

    void clear();
}

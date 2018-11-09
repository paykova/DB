package shampoo_company;

import java.io.Serializable;

public interface Label extends Serializable {


    int getId();
    void setId(int id);

    String getTitle();
    void setTitle(String title);

    String getSubtitle();
    void setSubtitle(String subtitle);
}

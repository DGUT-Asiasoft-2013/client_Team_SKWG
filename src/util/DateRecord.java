package util;

import java.util.Date;

public class DateRecord extends BaseEntity{
        Date createDate;
        Date editDate;
        
        public Date getCreateDate() {
                return createDate;
        }
        public void setCreateDate(Date createDate) {
                this.createDate = createDate;
        }
        public Date getEditDate() {
                return editDate;
        }
        public void setEditDate(Date editDate) {
                this.editDate = editDate;
        }
        
        
}

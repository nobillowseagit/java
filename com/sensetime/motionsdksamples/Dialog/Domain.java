package com.sensetime.motionsdksamples.Dialog;

import com.sensetime.motionsdksamples.Common.FaceServer;
import com.sensetime.motionsdksamples.Common.Person;

import static com.sensetime.motionsdksamples.Dialog.Domain.DOMAIN_TPYE.DOMAIN_GENEREL;

/**
 * Created by lyt on 2017/10/23.
 */

public class Domain {
    public enum DOMAIN_TPYE {
        DOMAIN_GENEREL, DOMAIN_USER, DOMAIN_GUESS_AGE, DOMAIN_PHOTOGRAPHY, DOMAIN_INTRODUCE,
        DOMAIN_HELLO, DOMAIN_CONTROL, DOMAIN_DETECT
    }

    public DOMAIN_TPYE type;
    public int subDomain;
    public DomainOperation operation;

    public Domain() {
        this.type = DOMAIN_GENEREL;
    }

    public Domain(DOMAIN_TPYE type) {
        this.type = type;
    }

    public String toString() {
        return this.type.toString();
    }

    public String toStringCN() {
        String string = null;
        switch (this.type) {
            case DOMAIN_GUESS_AGE:
                string = "猜年龄";
                break;
            case DOMAIN_INTRODUCE:
                string = "介绍";
                break;
            case DOMAIN_PHOTOGRAPHY:
                string = "全景拍照";
                break;
            case DOMAIN_USER:
                string = "用户";
                break;
            case DOMAIN_DETECT:
                string = "识别";
                break;
            case DOMAIN_GENEREL:
                string = "通用";
                break;
            case DOMAIN_CONTROL:
                string = "交互";
                break;
        }
        return string;
    }

    public DOMAIN_TPYE toDomainType() {
        DOMAIN_TPYE type = DOMAIN_GENEREL;

        return type;
    }

    public boolean compare(Domain domain) {
        if (this.type != domain.type) {
            return false;
        }
        return true;
    }

    public Person getCurrentPerson() {
        FaceServer faceServer = FaceServer.getInstance();
        return faceServer.getCurrentPerson();
    }

    /*
    public class SubDomain {
        public long id;

        public SubDomain() {
            id = UniqueId.getUid();
        }

        public long getSubDomainId() {
            return id;
        }

        public boolean compare(SubDomain subDomain) {
            if (this.id == subDomain.id) {
                return true;
            }
            return false;
        }
    }
    */

    public void setDomainOperation(DomainOperation operation) {
        this.operation = operation;
    }

    public interface DomainOperation {
        void preprocess(DialogContext context);
        void process(DialogContext context);
        void postprocess(DialogContext context);
    }
}

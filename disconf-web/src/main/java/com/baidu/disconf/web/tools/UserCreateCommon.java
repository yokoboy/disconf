package com.baidu.disconf.web.tools;

import java.util.ArrayList;
import java.util.List;

import com.baidu.disconf.web.service.user.model.UserBO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baidu.disconf.web.service.role.bo.RoleEnum;
import com.baidu.disconf.web.service.user.util.SignUtils;
import com.baidu.disconf.web.service.user.dao.UserDao;
import com.github.knightliao.apollo.utils.common.RandomUtil;

/**
 * @author knightliao
 */
public class UserCreateCommon {

    protected static final Logger LOG = LoggerFactory.getLogger(UserCreateCommon.class);

    /**
     * @param userName
     * @param password
     */
    public static void generateCreateSpecifyUserSQL(UserDao userDao, String userName, String password,
                                                    RoleEnum roleEnum, String ownAppIds) {

        UserBO userBO = new UserBO();

        userBO.setName(userName);

        userBO.setPassword(SignUtils.createPassword(password));
        // token
        userBO.setToken(SignUtils.createToken(userName));

        // set appids
//        userBO.setOwnApps(ownAppIds);

        // role
//        userBO.setRoleId(roleEnum.getValue());

        System.out.println("/* " + userName + "\t" + password + "*/");
        // userDao.create(userBO);

        List<UserBO> userBOList = new ArrayList<UserBO>();
        userBOList.add(userBO);

        printUserList(userBOList);
    }

    private static String getUserName(Long i) {

        return "testUser" + i;
    }

    /**
     * 生成内测用户
     */
    public static void generateCreateTestUserSQL(UserDao userDao) {

        System.out.println("\n");

        int num = 5;

        List<UserBO> userBOList = new ArrayList<UserBO>();
        for (Long i = 1L; i < num + 1; ++i) {

            UserBO userBO = new UserBO();

            userBO.setId(i);

            userBO.setName(getUserName(i));

//            userBO.setOwnApps("2");
//
//            userBO.setRoleId(RoleEnum.NORMAL.getValue());

            int random = RandomUtil.random(0, 10000);
            String password = "MhxzKhl" + String.valueOf(random);
            userBO.setPassword(SignUtils.createPassword(password));
            // token
            userBO.setToken(SignUtils.createToken(userBO.getName()));

            System.out.println("/* userid" + userBO.getId() + "\t" + password + "*/");
            // userDao.create(userBO);
            userBOList.add(userBO);
        }

        printUserList(userBOList);
    }

    /**
     * @param userBOList
     */
    private static void printUserList(List<UserBO> userBOList) {

        for (UserBO userBO : userBOList) {

            if (userBO.getId() != null) {
                System.out.format("DELETE FROM `userBO` where user_id=%d;\n", userBO.getId());
            }
            System.out
                    .format("INSERT INTO `userBO` (`user_id`, `name`, `password`, `token`, `ownapps`,`role_id`) VALUES "
                                    + "(%d,"
                                    +
                                    " '%s', " +
                                    "'%s', '%s','%s', '%d');\n", userBO.getId(), userBO.getName(), userBO.getPassword(),
//                            userBO.getToken(), userBO.getOwnApps(), userBO.getRoleId());
                            userBO.getToken(), null, null);
        }
        System.out.println("\n");
    }
}

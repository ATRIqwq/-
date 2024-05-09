import myAxios from "../plugins/myAxios";
import {getCurrentUserState} from "../states/userStates.ts";

/**
 * 获取用户信息
 * @returns {Promise<null|any>}
 */
export const getCurrentUser = async () => {
    const user = getCurrentUserState();
    if (user) {
        return user;
    }
    //从远程处获取用户信息
    const res = await myAxios.get("/user/current");
    if (res?.data?.code === 40100){
        const redirectUrl = window.location.href;
        window.location.href =`/user/login?redirect=${redirectUrl}`;
    }

    if (res.data.code === 0 ) {
        // setCurrentUserState(res.data);
        return res.data.data;
    }
    return null;
}

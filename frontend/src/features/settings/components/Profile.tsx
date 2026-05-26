import Image from "next/image";

import profile from "../assets/profile.svg";

export const Profile = () => {
  return (
    <div className="mt-10 max-md:mt-8" id="profile">
      <div className="flex items-center gap-2">
        <Image src={profile} alt="" width={30} height={30} className="max-md:h-6 max-md:w-6" />
        <p className="font-medium max-md:text-[13px]">プロフィール</p>
      </div>
      <div className="bg-white-back border-brown-dark mt-4 flex gap-5 rounded-lg border py-6 pr-4 pl-10 max-md:mt-3 max-md:flex-col max-md:items-center max-md:p-5">
        <div className="w-fit">
          <div className="mx-auto h-20 w-20 shrink-0 rounded-full bg-[linear-gradient(100deg,#FFC0AB_35%,#FFF829_65%)] p-px">
            <Image
              src="/images/mock-img.jpg"
              alt=""
              width={80}
              height={80}
              className="aspect-square h-full w-full rounded-full object-cover"
            />
          </div>
          <button className="mt-2 text-xs text-nowrap underline max-md:text-[10px]">
            アイコンを編集する
          </button>
        </div>
        <div className="w-full">
          <div className="flex items-center gap-6 max-md:gap-4">
            <p className="max-md:text-[13px]">表示名：まま</p>
            <button className="text-xs underline max-md:text-[10px]">編集する</button>
          </div>
          <p className="mt-2 max-md:mt-1 max-md:text-[13px]">メールアドレス：parent@example.com</p>
          <p className="mt-8 ml-auto text-right text-xs max-md:mt-4 max-md:ml-0 max-md:text-left max-md:text-[10px]">
            メールアドレス、パスワードの変更は連絡してください
          </p>
        </div>
      </div>
    </div>
  );
};

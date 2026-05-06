import Image from "next/image";

import icon from "../assets/image-icon.svg";

export const ImageUploader = () => {
  return (
    <div className="bg-brown-back border-brown-middle relative block rounded-4xl border-2 border-dotted px-5 pt-10 pb-15 text-center max-md:rounded-2xl max-md:pb-5">
      <p className="text-accent-pink bg-accent-pink-back absolute top-5 right-8 grid h-8 w-25 place-content-center rounded-2xl border text-xs font-medium max-md:top-2 max-md:right-2 max-md:h-5 max-md:w-18 max-md:text-[10px]">
        複数選択可
      </p>
      <Image
        src={icon}
        alt=""
        width={46}
        height={46}
        className="mx-auto max-md:h-8.5 max-md:w-8.5"
      />
      <p className="mt-5 text-xl font-medium max-md:mt-2.5 max-md:text-[13px]">
        写真をアップロード
      </p>
      <p className="text-note-gray mt-4 text-sm max-md:hidden">
        複数枚をまとめて選択できます
        <br />
        ドラッグ＆ドロップにも対応
      </p>
      <button className="bg-brown-middle mt-5 h-10 w-40 rounded-4xl font-medium text-white max-md:hidden">
        写真を選択
      </button>
      <p className="text-note-gray mt-4 text-xs max-md:hidden">
        jpg / jpeg / png / heic - 1枚最大50MB
      </p>
      <p className="text-note-gray mt-2 text-[10px] leading-5 md:hidden">
        まとめて選択できます
        <br />
        1枚最大50MB
      </p>
    </div>
  );
};

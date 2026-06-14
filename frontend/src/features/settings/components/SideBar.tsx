"use client";
import Image from "next/image";
import { useEffect, useState } from "react";

import account from "../assets/sidebar/account.svg";
import arrow from "../assets/sidebar/arrow.svg";
import displayMode from "../assets/sidebar/display-mode.svg";
import members from "../assets/sidebar/members.svg";
import profile from "../assets/sidebar/profile.svg";
import sharingGroups from "../assets/sidebar/sharing-groups.svg";
import tags from "../assets/sidebar/tags.svg";

const items = [
  { id: "profile", label: "プロフィール", icon: profile },
  { id: "members", label: "メンバー一覧", icon: members },
  { id: "tags", label: "タグ", icon: tags },
  { id: "sharing-groups", label: "共有範囲", icon: sharingGroups },
  { id: "display-mode", label: "表示モード", icon: displayMode },
  { id: "account", label: "アカウント", icon: account },
];

export const Sidebar = () => {
  const [activeId, setActiveId] = useState<string | null>(null);

  useEffect(() => {
    const observer = new IntersectionObserver(
      // 可視領域に入った要素の中で一番上にあるもののIDをstateに保存する
      (entries) => {
        const visible = entries
          .filter((e) => e.isIntersecting)
          .sort((a, b) => a.boundingClientRect.top - b.boundingClientRect.top)[0];
        if (visible) setActiveId(visible.target.id);
      },
      {
        // ビューポート上部20%の位置で切り替え
        rootMargin: "-20% 0px -70% 0px",
        threshold: 0,
      },
    );

    // 各コンテンツを監視対象にする
    items.forEach(({ id }) => {
      const el = document.getElementById(id);
      if (el) observer.observe(el);
    });

    // クリーンアップ
    return () => observer.disconnect();
  }, []);

  return (
    <div className="border-brown-dark max-md:bg-background sticky top-20 z-10 w-50 shrink-0 max-lg:w-40 max-md:top-0 max-md:-ml-5 max-md:w-screen max-md:overflow-x-scroll max-md:border-t max-md:border-b">
      <div className="grid gap-3 max-md:flex">
        {items.map(({ id, label, icon }) => {
          const isActive = activeId === id;
          return (
            <a
              key={id}
              href={`#${id}`}
              className={`group hover:bg-brown-back relative flex h-11 cursor-pointer items-center gap-2 rounded-lg px-5 transition-all max-lg:px-2 max-md:h-9 max-md:shrink-0 max-md:gap-1 max-md:rounded-none max-md:px-3 ${isActive ? "bg-brown-back" : ""}`}
            >
              <Image src={icon} alt="" width={30} height={30} className="max-md:h-5 max-md:w-5" />
              <p className="text-brown-dark text-[15px] text-nowrap max-lg:text-sm max-md:text-xs">
                {label}
              </p>
              <Image
                src={arrow}
                alt=""
                width={4}
                height={8}
                className={`absolute top-0 right-3 bottom-0 my-auto transition-all group-hover:opacity-100 max-md:hidden ${isActive ? "opacity-100" : "opacity-0"}`}
              />
            </a>
          );
        })}
      </div>
    </div>
  );
};

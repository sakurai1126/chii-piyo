"use client";

import Image from "next/image";
import { useEffect, useState } from "react";

import { cn } from "@/utils/cn";

import account from "../assets/sidebar/account.svg";
import albums from "../assets/sidebar/albums.svg";
import arrow from "../assets/sidebar/arrow.svg";
import displayMode from "../assets/sidebar/display-mode.svg";
import members from "../assets/sidebar/members.svg";
import profile from "../assets/sidebar/profile.svg";
import sharingGroups from "../assets/sidebar/sharing-groups.svg";
import tags from "../assets/sidebar/tags.svg";
import whiteAccount from "../assets/white/account.svg";
import whiteAlbums from "../assets/white/albums.svg";
import whiteArrow from "../assets/white/arrow.svg";
import whiteDisplayMode from "../assets/white/display-mode.svg";
import whiteMembers from "../assets/white/members.svg";
import whiteProfile from "../assets/white/profile.svg";
import whiteSharingGroups from "../assets/white/sharing-groups.svg";
import whiteTags from "../assets/white/tags.svg";

const items = [
  { id: "profile", label: "プロフィール", icon: profile, whiteIcon: whiteProfile },
  { id: "members", label: "メンバー一覧", icon: members, whiteIcon: whiteMembers },
  { id: "tags", label: "タグ", icon: tags, whiteIcon: whiteTags },
  { id: "sharing-groups", label: "共有範囲", icon: sharingGroups, whiteIcon: whiteSharingGroups },
  { id: "albums", label: "アルバム", icon: albums, whiteIcon: whiteAlbums },
  { id: "display-mode", label: "表示モード", icon: displayMode, whiteIcon: whiteDisplayMode },
  { id: "account", label: "アカウント", icon: account, whiteIcon: whiteAccount },
];

const isAdminItems = new Set(["tags", "sharing-groups", "albums"]);

type Props = {
  isAdmin: boolean;
};

export const Sidebar = ({ isAdmin }: Props) => {
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
    <div className="border-brown-dark sticky top-20 z-100 w-50 shrink-0 @max-lg:w-40 @max-md:hidden">
      <div className="grid gap-3">
        {items.map((item) => {
          const isActive = activeId === item.id;
          if (!isAdmin && isAdminItems.has(item.id)) return null;

          return (
            <a
              key={item.id}
              href={`#${item.id}`}
              className={cn(
                "group hover:bg-background-accent relative flex h-11 cursor-pointer items-center gap-2 rounded-lg px-5 transition-all @max-lg:px-2",
                isActive && "bg-background-accent",
              )}
            >
              <Image src={item.icon} alt="" width={30} height={30} className="dark:hidden" />
              <Image
                src={item.whiteIcon}
                alt=""
                width={30}
                height={30}
                className="hidden dark:block"
              />
              <p className="text-brown-dark text-[15px] text-nowrap @max-lg:text-sm dark:text-white">
                {item.label}
              </p>
              <Image
                src={arrow}
                alt=""
                width={4}
                height={8}
                className={cn(
                  "absolute top-0 right-3 bottom-0 my-auto transition-all group-hover:opacity-100 dark:hidden",
                  isActive ? "opacity-100" : "opacity-0",
                )}
              />
              <Image
                src={whiteArrow}
                alt=""
                width={4}
                height={8}
                className={cn(
                  "absolute top-0 right-3 bottom-0 my-auto hidden transition-all group-hover:opacity-100 dark:block",
                  isActive ? "opacity-100" : "opacity-0",
                )}
              />
            </a>
          );
        })}
      </div>
    </div>
  );
};

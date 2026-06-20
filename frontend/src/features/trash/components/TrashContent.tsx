import { Button } from "@/components/ui/Button";
import { TrashItemListResponseDto } from "@/lib/api-client/gen";

import { TrashItem } from "./TrashItem";

type Props = {
  trashItems: TrashItemListResponseDto;
};
export const TrashContent = ({ trashItems }: Props) => {
  return (
    <>
      <div className="mt-10 flex gap-10 max-md:mt-8 max-md:flex-col-reverse max-md:gap-6">
        <label htmlFor="allCheck" className="flex cursor-pointer items-center gap-3">
          <input
            type="checkbox"
            name=""
            id="allCheck"
            className="accent-accent-pink h-4.5 w-4.5 max-md:h-4 max-md:w-4"
          />
          <p className="text-lg max-md:text-[13px]">すべて選択</p>
        </label>
        <div className="flex gap-3 max-md:flex-col">
          <Button variant="cancel" className="w-fit px-4">
            選択したメディアを復元
          </Button>
          <Button variant="remove" className="w-fit px-4">
            選択したメディアを完全に削除
          </Button>
        </div>
      </div>
      <div className="mt-10 grid gap-5 max-md:mt-8">
        {trashItems.items.map((trashItem) => (
          <TrashItem key={trashItem.id} trashItem={trashItem} />
        ))}
      </div>
      <div className="border-line-gray mt-10 flex items-center justify-between border-t pt-7 max-md:flex-col max-md:items-start">
        <div>
          <p className="text-warning text-sm max-md:text-xs">ゴミ箱を空にする</p>
          <p className="mt-2 text-sm max-md:text-xs">
            すべてのメディアを完全に削除します。
            <br className="md:hidden" />
            この操作は取り消せません。
          </p>
        </div>
        <Button variant="remove" className="w-fit px-6 max-md:mt-4 max-md:ml-auto">
          ゴミ箱を空にする
        </Button>
      </div>
    </>
  );
};

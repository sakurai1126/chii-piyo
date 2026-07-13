import BackGround from "@/components/layout/BackGround";
import { BottomNavigation } from "@/components/layout/BottomNavigation";
import Footer from "@/components/layout/Footer";
import Header from "@/components/layout/Header";
import { isAdminUser, isEasyMode } from "@/features/auth";

type Props = {
  children: React.ReactNode;
  modal: React.ReactNode;
};

export default async function MainLayout({ children, modal }: Readonly<Props>) {
  const [isAdmin, isEasy] = await Promise.all([isAdminUser(), isEasyMode()]);

  return (
    <BackGround>
      <Header />
      {children}
      {modal}
      <BottomNavigation isAdmin={isAdmin} isEasy={isEasy} />
      <Footer />
    </BackGround>
  );
}

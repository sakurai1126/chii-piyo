import BackGround from "@/components/layout/BackGround";
import BottomNavigation from "@/components/layout/BottomNavigation";
import Footer from "@/components/layout/Footer";
import Header from "@/components/layout/Header";

type Props = {
  children: React.ReactNode;
  modal: React.ReactNode;
};

export default function MainLayout({ children, modal }: Readonly<Props>) {
  return (
    <BackGround>
      <Header />
      {children}
      {modal}
      <BottomNavigation />
      <Footer />
    </BackGround>
  );
}

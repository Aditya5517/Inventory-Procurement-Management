"use client";

import Link from "next/link";
import { useRouter } from "next/navigation";

export default function Sidebar() {
  const router = useRouter();

  function logout() {
    localStorage.removeItem("token");
    router.push("/login");
  }

  return (
    <aside className="flex min-h-screen w-64 flex-col border-r bg-white p-5">
      <h1 className="mb-8 text-xl font-bold text-black">
        Inventory & Procurement
      </h1>

      <nav className="flex flex-col gap-2 text-gray-700">
        <Link
          href="/dashboard"
          className="rounded px-3 py-2 hover:bg-gray-100"
        >
          Dashboard
        </Link>

        <Link
          href="/products"
          className="rounded px-3 py-2 hover:bg-gray-100"
        >
          Products
        </Link>

        <Link
          href="/categories"
          className="rounded px-3 py-2 hover:bg-gray-100"
        >
          Categories
        </Link>

        <Link
          href="/suppliers"
          className="rounded px-3 py-2 hover:bg-gray-100"
        >
          Suppliers
        </Link>

        <Link
          href="/purchase-requests"
          className="rounded px-3 py-2 hover:bg-gray-100"
        >
          Purchase Requests
        </Link>

        <Link
          href="/purchase-orders"
          className="rounded px-3 py-2 hover:bg-gray-100"
        >
          Purchase Orders
        </Link>

        <Link
          href="/goods-receipts"
          className="rounded px-3 py-2 hover:bg-gray-100"
        >
          Goods Receipts
        </Link>

        <Link
          href="/stock-movements"
          className="rounded px-3 py-2 hover:bg-gray-100"
        >
          Stock Movements
        </Link>

        <Link
          href="/notifications"
          className="rounded px-3 py-2 hover:bg-gray-100"
        >
          Notifications
        </Link>
      </nav>

      <button
        onClick={logout}
        className="mt-auto rounded border border-gray-300 px-3 py-2 text-left text-red-600 hover:bg-gray-100"
      >
        Logout
      </button>
    </aside>
  );
}
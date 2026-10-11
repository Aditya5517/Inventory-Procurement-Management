"use client";

import { useEffect, useState } from "react";
import AppLayout from "@/components/AppLayout";
import { apiRequest } from "@/lib/api";

type Supplier = {
  id: number;
  companyName: string;
  contactPerson: string;
  email: string;
  phone: string;
  address: string;
  isActive: boolean;
};

export default function SuppliersPage() {
  const [suppliers, setSuppliers] = useState<Supplier[]>([]);
  const [error, setError] = useState("");

  useEffect(() => {
    loadSuppliers();
  }, []);

  async function loadSuppliers() {
    try {
      const data = await apiRequest("/api/suppliers");
      setSuppliers(data);
    } catch (error) {
      console.error(error);
      setError("Could not load suppliers");
    }
  }

  return (
    <AppLayout>
      <h1 className="text-3xl font-bold text-black">
        Suppliers
      </h1>

      <p className="mt-2 text-gray-600">
        Registered suppliers
      </p>

      {error && (
        <p className="mt-4 text-red-600">
          {error}
        </p>
      )}

      <div className="mt-6 overflow-hidden rounded-lg border bg-white">
        <table className="w-full text-left">
          <thead className="bg-gray-100 text-black">
            <tr>
              <th className="p-3">
                Company
              </th>

              <th className="p-3">
                Contact Person
              </th>

              <th className="p-3">
                Email
              </th>

              <th className="p-3">
                Phone
              </th>

              <th className="p-3">
                Status
              </th>
            </tr>
          </thead>

          <tbody className="text-gray-700">
            {suppliers.map((supplier) => (
              <tr
                key={supplier.id}
                className="border-t"
              >
                <td className="p-3">
                  {supplier.companyName}
                </td>

                <td className="p-3">
                  {supplier.contactPerson}
                </td>

                <td className="p-3">
                  {supplier.email}
                </td>

                <td className="p-3">
                  {supplier.phone}
                </td>

                <td className="p-3">
                  {supplier.isActive
                    ? "Active"
                    : "Inactive"}
                </td>
              </tr>
            ))}
          </tbody>
        </table>

        {suppliers.length === 0 && (
          <p className="p-6 text-gray-500">
            No suppliers found.
          </p>
        )}
      </div>
    </AppLayout>
  );
}
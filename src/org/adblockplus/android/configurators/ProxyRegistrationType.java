/*
 * This file is part of Adblock Plus <https://adblockplus.org/>,
 * Copyright (C) 2006-2016 Eyeo GmbH
 *
 * Adblock Plus is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3 as
 * published by the Free Software Foundation.
 *
 * Adblock Plus is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Adblock Plus.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.adblockplus.android.configurators;

import android.util.Log;
import org.adblockplus.android.ProxyServerType;

public enum ProxyRegistrationType
{
  UNKNOWN(ProxyServerType.UNKNOWN, false),
  CYANOGENMOD(ProxyServerType.HTTPS, true),
  IPTABLES(ProxyServerType.HTTP, true),
  NATIVE(ProxyServerType.HTTPS, true),
  MANUAL(ProxyServerType.HTTPS, false);

  private final ProxyServerType proxyType;
  private final boolean autoConfigured;
  private static final String TAG = "ProxyType";

  private ProxyRegistrationType(final ProxyServerType proxyType, final boolean autoConfigured)
  {
    this.proxyType = proxyType;
    this.autoConfigured = autoConfigured;
  }

  public ProxyServerType getProxyType()
  {
    Log.d(TAG, proxyType.toString() + " auto configured: " + autoConfigured);
    return this.proxyType;
  }

  public boolean isAutoConfigured()
  {
    return this.autoConfigured;
  }
}
